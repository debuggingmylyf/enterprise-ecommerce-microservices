package com.ecommerce.payment.service.serviceImpl;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.payment.client.OrderServiceClient;
import com.ecommerce.payment.client.dto.OrderClientResponse;
import com.ecommerce.payment.dto.request.InitiatePaymentRequest;
import com.ecommerce.payment.dto.request.RefundRequest;
import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.dto.response.PaymentSummaryResponse;
import com.ecommerce.payment.dto.response.RefundResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.Refund;
import com.ecommerce.payment.enums.PaymentStatus;
import com.ecommerce.payment.enums.RefundStatus;
import com.ecommerce.payment.mapper.PaymentMapper;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.repository.RefundRepository;
import com.ecommerce.payment.service.PaymentProvider;
import com.ecommerce.payment.service.PaymentProvider.PaymentProviderResult;
import com.ecommerce.payment.service.PaymentProvider.RefundProviderResult;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link PaymentService} interface.
 *
 * <p>Orchestrates payment processing through a pluggable
 * {@link PaymentProvider} (Stripe by default), communicates with the
 * Order Service via Feign for payment callbacks, and manages the full
 * payment + refund lifecycle.</p>
 *
 * <p><b>Payment Flow:</b></p>
 * <ol>
 *     <li>Idempotency check → return existing if duplicate key</li>
 *     <li>Validate order via Order Service (exists, belongs to user, in CREATED state)</li>
 *     <li>Create Payment record (INITIATED)</li>
 *     <li>Call PaymentProvider.processPayment()</li>
 *     <li>On SUCCESS: update payment, call order-service /payment-success</li>
 *     <li>On REQUIRES_ACTION: save Stripe PaymentIntent details, return clientSecret</li>
 *     <li>On FAILURE: update payment, call order-service /payment-failed</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final OrderServiceClient orderServiceClient;
    private final PaymentProvider paymentProvider;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiatePayment(final InitiatePaymentRequest request, final UUID userId) {
        log.info("Initiating payment for order: {} by user: {} method: {}",
                request.getOrderId(), userId, request.getPaymentMethod());

        // 1. Idempotency check — return existing payment if duplicate
        final Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingPayment.isPresent()) {
            log.info("Duplicate idempotency key detected: {}. Returning existing payment.", request.getIdempotencyKey());
            return paymentMapper.toPaymentResponse(existingPayment.get());
        }

        // 2. Check if order already has a successful payment
        final Optional<Payment> existingOrderPayment = paymentRepository.findByOrderId(request.getOrderId());
        if (existingOrderPayment.isPresent() && existingOrderPayment.get().getStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("PAYMENT_ALREADY_PROCESSED",
                    "Payment already completed for order: " + request.getOrderId());
        }

        // 3. Validate order exists via Order Service
        final ResponseEntity<OrderClientResponse> orderRes = orderServiceClient.getOrderById(request.getOrderId());
        if (orderRes == null || orderRes.getBody() == null) {
            throw new ResourceNotFoundException("ORDER_NOT_FOUND",
                    "Order not found: " + request.getOrderId());
        }

        final OrderClientResponse order = orderRes.getBody();

        // Verify order belongs to the requesting user
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED",
                    "Not authorized to pay for this order");
        }

        // Verify order is in a payable state
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException("INVALID_PAYMENT_STATE",
                    "Order is not in a payable state. Current status: " + order.getStatus());
        }

        // 4. Create Payment entity (INITIATED)
        final Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(userId)
                .amount(order.getTotalAmount())
                .currency("INR")
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.INITIATED)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        final Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment record created: {} with status INITIATED", savedPayment.getId());

        // 5. Process payment via provider (PROCESSING)
        savedPayment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(savedPayment);

        final PaymentProviderResult result = paymentProvider.processPayment(savedPayment);

        // 6. Handle provider result
        if (result.success()) {
            return handlePaymentSuccess(savedPayment, result);
        } else if (result.requiresAction()) {
            return handlePaymentRequiresAction(savedPayment, result);
        } else {
            return handlePaymentFailure(savedPayment, result);
        }
    }

    /**
     * Payment succeeded (auto-confirmed in test mode, or COD).
     */
    private PaymentResponse handlePaymentSuccess(final Payment payment, final PaymentProviderResult result) {
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(result.transactionId());
        payment.setStripePaymentIntentId(result.paymentIntentId());
        payment.setProviderResponse(result.rawResponse());
        paymentRepository.save(payment);

        log.info("Payment {} SUCCESS — txnId={}, piId={}", payment.getId(), result.transactionId(), result.paymentIntentId());

        // Notify order-service: payment success
        try {
            orderServiceClient.markPaymentSuccess(payment.getOrderId());
            log.info("Order {} notified of payment success", payment.getOrderId());
        } catch (final Exception ex) {
            log.error("Failed to notify order service of payment success for order {}: {}",
                    payment.getOrderId(), ex.getMessage());
        }

        return paymentMapper.toPaymentResponse(payment);
    }

    /**
     * Payment requires frontend action (3D Secure, UPI redirect, etc.).
     * Returns clientSecret for Stripe.js confirmation.
     */
    private PaymentResponse handlePaymentRequiresAction(final Payment payment, final PaymentProviderResult result) {
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setStripePaymentIntentId(result.paymentIntentId());
        payment.setClientSecret(result.clientSecret());
        payment.setProviderResponse(result.rawResponse());
        paymentRepository.save(payment);

        log.info("Payment {} REQUIRES_ACTION — piId={}, clientSecret returned to frontend",
                payment.getId(), result.paymentIntentId());

        return paymentMapper.toPaymentResponse(payment);
    }

    /**
     * Payment failed at the provider level.
     */
    private PaymentResponse handlePaymentFailure(final Payment payment, final PaymentProviderResult result) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(result.failureReason());
        payment.setStripePaymentIntentId(result.paymentIntentId());
        payment.setProviderResponse(result.rawResponse());
        paymentRepository.save(payment);

        log.info("Payment {} FAILED — reason: {}", payment.getId(), result.failureReason());

        // Notify order-service: payment failed
        try {
            orderServiceClient.markPaymentFailed(payment.getOrderId());
            log.info("Order {} notified of payment failure", payment.getOrderId());
        } catch (final Exception ex) {
            log.error("Failed to notify order service of payment failure for order {}: {}",
                    payment.getOrderId(), ex.getMessage());
        }

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(final UUID paymentId, final UUID userId, final String userRole) {
        log.debug("Fetching payment: {}", paymentId);
        final Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_NOT_FOUND",
                        "Payment not found with id: " + paymentId));

        if (!userRole.contains("ADMIN") && !payment.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "Not authorized to view this payment");
        }

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(final UUID orderId, final UUID userId, final String userRole) {
        log.debug("Fetching payment for order: {}", orderId);
        final Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_NOT_FOUND",
                        "Payment not found for order: " + orderId));

        if (!userRole.contains("ADMIN") && !payment.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "Not authorized to view this payment");
        }

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentSummaryResponse> getMyPayments(final UUID userId) {
        log.debug("Fetching payments for user: {}", userId);
        final List<Payment> payments = paymentRepository.findAllByUserId(userId);
        return payments.stream().map(paymentMapper::toPaymentSummaryResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PaymentSummaryResponse> getAllPayments(final Pageable pageable) {
        log.debug("Fetching all payments paginated");
        final Page<Payment> paymentPage = paymentRepository.findAll(pageable);
        final Page<PaymentSummaryResponse> summaryPage = paymentPage.map(paymentMapper::toPaymentSummaryResponse);

        final String sortBy = pageable.getSort().isSorted()
                ? pageable.getSort().iterator().next().getProperty()
                : "createdAt";
        final String sortDirection = pageable.getSort().isSorted()
                ? pageable.getSort().iterator().next().getDirection().name()
                : "DESC";

        return PaginatedResponse.of(summaryPage, sortBy, sortDirection);
    }

    @Override
    @Transactional
    public RefundResponse initiateRefund(final RefundRequest request, final UUID userId, final String userRole) {
        log.info("Initiating refund for payment: {} amount: {}", request.getPaymentId(), request.getAmount());

        final Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_NOT_FOUND",
                        "Payment not found with id: " + request.getPaymentId()));

        // Only successful payments can be refunded
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("INVALID_PAYMENT_STATE",
                    "Cannot refund payment in state: " + payment.getStatus());
        }

        // Calculate total already refunded
        final List<Refund> existingRefunds = refundRepository.findAllByPaymentId(payment.getId());
        final BigDecimal totalRefunded = existingRefunds.stream()
                .filter(r -> r.getStatus() == RefundStatus.SUCCESS)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal remainingRefundable = payment.getAmount().subtract(totalRefunded);
        if (request.getAmount().compareTo(remainingRefundable) > 0) {
            throw new BusinessException("REFUND_EXCEEDS_AMOUNT",
                    "Refund amount " + request.getAmount() + " exceeds remaining refundable amount: " + remainingRefundable);
        }

        // Create refund entity
        final Refund refund = Refund.builder()
                .payment(payment)
                .amount(request.getAmount())
                .status(RefundStatus.INITIATED)
                .reason(request.getReason())
                .build();

        final Refund savedRefund = refundRepository.save(refund);

        // Process refund via provider (Stripe refund or COD local refund)
        final RefundProviderResult result = paymentProvider.processRefund(savedRefund, payment);

        if (result.success()) {
            savedRefund.setStatus(RefundStatus.SUCCESS);
            savedRefund.setRefundTransactionId(result.refundTransactionId());
            refundRepository.save(savedRefund);

            // Check if full refund — update payment status
            final BigDecimal newTotalRefunded = totalRefunded.add(request.getAmount());
            if (newTotalRefunded.compareTo(payment.getAmount()) >= 0) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
                log.info("Payment {} fully refunded", payment.getId());
            }
        } else {
            savedRefund.setStatus(RefundStatus.FAILED);
            refundRepository.save(savedRefund);
            log.warn("Refund failed for payment {}: {}", payment.getId(), result.failureReason());
        }

        return paymentMapper.toRefundResponse(savedRefund);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getRefundsByPaymentId(final UUID paymentId) {
        log.debug("Fetching refunds for payment: {}", paymentId);
        final List<Refund> refunds = refundRepository.findAllByPaymentId(paymentId);
        return refunds.stream().map(paymentMapper::toRefundResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderIdInternal(final UUID orderId) {
        log.debug("Internal: Fetching payment for order: {}", orderId);
        final Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_NOT_FOUND",
                        "Payment not found for order: " + orderId));
        return paymentMapper.toPaymentResponse(payment);
    }
}
