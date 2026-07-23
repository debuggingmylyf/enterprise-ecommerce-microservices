package com.ecommerce.payment.service;

import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.payment.dto.request.InitiatePaymentRequest;
import com.ecommerce.payment.dto.request.RefundRequest;
import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.dto.response.PaymentSummaryResponse;
import com.ecommerce.payment.dto.response.RefundResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing payments.
 */
public interface PaymentService {

    /**
     * Initiates a payment for the given order.
     *
     * @param request the payment initiation request
     * @param userId  the UUID of the user initiating the payment
     * @return the payment response
     */
    PaymentResponse initiatePayment(InitiatePaymentRequest request, UUID userId);

    /**
     * Retrieves a payment by its UUID.
     *
     * @param paymentId the UUID of the payment
     * @param userId    the requesting user's UUID
     * @param userRole  the requesting user's role
     * @return the payment response
     */
    PaymentResponse getPaymentById(UUID paymentId, UUID userId, String userRole);

    /**
     * Retrieves a payment by order UUID.
     *
     * @param orderId  the UUID of the order
     * @param userId   the requesting user's UUID
     * @param userRole the requesting user's role
     * @return the payment response
     */
    PaymentResponse getPaymentByOrderId(UUID orderId, UUID userId, String userRole);

    /**
     * Retrieves all payments for the currently authenticated user.
     *
     * @param userId the UUID of the user
     * @return a list of payment summaries
     */
    List<PaymentSummaryResponse> getMyPayments(UUID userId);

    /**
     * Retrieves all payments in the system with pagination (Admin only).
     *
     * @param pageable pagination options
     * @return a paginated list of payment summaries
     */
    PaginatedResponse<PaymentSummaryResponse> getAllPayments(Pageable pageable);

    /**
     * Initiates a refund for a payment (Admin only).
     *
     * @param request  the refund request
     * @param userId   the admin user's UUID
     * @param userRole the admin user's role
     * @return the refund response
     */
    RefundResponse initiateRefund(RefundRequest request, UUID userId, String userRole);

    /**
     * Retrieves all refunds for a given payment.
     *
     * @param paymentId the UUID of the payment
     * @return a list of refund responses
     */
    List<RefundResponse> getRefundsByPaymentId(UUID paymentId);

    /**
     * Internal method: retrieves payment status by order ID (for inter-service calls).
     *
     * @param orderId the UUID of the order
     * @return the payment response
     */
    PaymentResponse getPaymentByOrderIdInternal(UUID orderId);
}
