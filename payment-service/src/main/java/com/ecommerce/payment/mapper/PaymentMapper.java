package com.ecommerce.payment.mapper;

import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.dto.response.PaymentSummaryResponse;
import com.ecommerce.payment.dto.response.RefundResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.Refund;
import org.springframework.stereotype.Component;

/**
 * Spring-managed mapper responsible for mapping Payment and Refund entities
 * to response DTOs. Manual mapping is preferred.
 */
@Component
public class PaymentMapper {

    /**
     * Map Payment entity to full PaymentResponse DTO.
     *
     * @param payment the source entity
     * @return PaymentResponse DTO
     */
    public PaymentResponse toPaymentResponse(final Payment payment) {
        if (payment == null) {
            return null;
        }
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .failureReason(payment.getFailureReason())
                .idempotencyKey(payment.getIdempotencyKey())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .clientSecret(payment.getClientSecret())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    /**
     * Map Payment entity to lightweight PaymentSummaryResponse DTO.
     *
     * @param payment the source entity
     * @return PaymentSummaryResponse DTO
     */
    public PaymentSummaryResponse toPaymentSummaryResponse(final Payment payment) {
        if (payment == null) {
            return null;
        }
        return PaymentSummaryResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    /**
     * Map Refund entity to RefundResponse DTO.
     *
     * @param refund the source entity
     * @return RefundResponse DTO
     */
    public RefundResponse toRefundResponse(final Refund refund) {
        if (refund == null) {
            return null;
        }
        return RefundResponse.builder()
                .id(refund.getId())
                .paymentId(refund.getPayment().getId())
                .amount(refund.getAmount())
                .status(refund.getStatus())
                .reason(refund.getReason())
                .refundTransactionId(refund.getRefundTransactionId())
                .createdAt(refund.getCreatedAt())
                .build();
    }
}
