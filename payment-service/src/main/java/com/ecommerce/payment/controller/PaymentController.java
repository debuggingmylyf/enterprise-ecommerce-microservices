package com.ecommerce.payment.controller;

import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.payment.dto.request.InitiatePaymentRequest;
import com.ecommerce.payment.dto.request.RefundRequest;
import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.dto.response.PaymentSummaryResponse;
import com.ecommerce.payment.dto.response.RefundResponse;
import com.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing endpoints for managing payments.
 *
 * <p>Base path: {@code /api/v1/payments}</p>
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Initiates a payment for an order.
     *
     * @param userIdHeader the authenticated user's ID forwarded from gateway
     * @param request      the validated payment initiation payload
     * @return {@code 201 Created} with payment details
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestHeader(name = "X-User-Id") final String userIdHeader,
            @Valid @RequestBody final InitiatePaymentRequest request) {

        log.info("POST /api/v1/payments – initiating payment for order {} by user {}", request.getOrderId(), userIdHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final PaymentResponse response = paymentService.initiatePayment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a payment by its ID.
     *
     * @param id             the UUID of the payment
     * @param userIdHeader   the authenticated user's ID
     * @param userRoleHeader the authenticated user's role
     * @return {@code 200 OK} with payment details
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable final UUID id,
            @RequestHeader(name = "X-User-Id") final String userIdHeader,
            @RequestHeader(name = "X-User-Role") final String userRoleHeader) {

        log.debug("GET /api/v1/payments/{} – request from user {} with role {}", id, userIdHeader, userRoleHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final PaymentResponse response = paymentService.getPaymentById(id, userId, userRoleHeader);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a payment by its order ID.
     *
     * @param orderId        the UUID of the order
     * @param userIdHeader   the authenticated user's ID
     * @param userRoleHeader the authenticated user's role
     * @return {@code 200 OK} with payment details
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable final UUID orderId,
            @RequestHeader(name = "X-User-Id") final String userIdHeader,
            @RequestHeader(name = "X-User-Role") final String userRoleHeader) {

        log.debug("GET /api/v1/payments/order/{} – request from user {}", orderId, userIdHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final PaymentResponse response = paymentService.getPaymentByOrderId(orderId, userId, userRoleHeader);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all payments belonging to the currently authenticated user.
     *
     * @param userIdHeader the authenticated user's ID
     * @return {@code 200 OK} with a list of payment summaries
     */
    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentSummaryResponse>> getMyPayments(
            @RequestHeader(name = "X-User-Id") final String userIdHeader) {

        log.debug("GET /api/v1/payments/my-payments – fetching payments for user {}", userIdHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final List<PaymentSummaryResponse> response = paymentService.getMyPayments(userId);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    // Admin Endpoints
    // -------------------------------------------------------------------------

    /**
     * Returns a paginated list of all payments in the system (Admin only).
     *
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 20)
     * @param sortBy    field to sort by (default: {@code createdAt})
     * @param direction sort direction, {@code ASC} or {@code DESC} (default: {@code DESC})
     * @return {@code 200 OK} with a paginated envelope of payment summaries
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<PaymentSummaryResponse>> getAllPayments(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(defaultValue = "createdAt") final String sortBy,
            @RequestParam(defaultValue = "DESC") final String direction) {

        log.info("GET /api/v1/payments – page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        final Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);
        final PaginatedResponse<PaymentSummaryResponse> response = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Initiates a refund for a payment (Admin only).
     *
     * @param userIdHeader   the admin user's ID
     * @param userRoleHeader the admin user's role
     * @param request        the validated refund request payload
     * @return {@code 201 Created} with refund details
     */
    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> initiateRefund(
            @RequestHeader(name = "X-User-Id") final String userIdHeader,
            @RequestHeader(name = "X-User-Role") final String userRoleHeader,
            @Valid @RequestBody final RefundRequest request) {

        log.info("POST /api/v1/payments/refund – refunding {} for payment {}", request.getAmount(), request.getPaymentId());
        final UUID userId = UUID.fromString(userIdHeader);
        final RefundResponse response = paymentService.initiateRefund(request, userId, userRoleHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all refunds for a given payment.
     *
     * @param paymentId the UUID of the payment
     * @return {@code 200 OK} with a list of refund responses
     */
    @GetMapping("/{paymentId}/refunds")
    public ResponseEntity<List<RefundResponse>> getRefundsByPaymentId(
            @PathVariable final UUID paymentId) {

        log.debug("GET /api/v1/payments/{}/refunds", paymentId);
        final List<RefundResponse> response = paymentService.getRefundsByPaymentId(paymentId);
        return ResponseEntity.ok(response);
    }
}
