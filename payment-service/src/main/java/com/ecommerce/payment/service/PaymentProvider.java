package com.ecommerce.payment.service;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.Refund;

/**
 * Strategy interface for pluggable payment providers.
 *
 * <p>The default implementation is
 * {@link com.ecommerce.payment.service.serviceImpl.StripePaymentProvider}
 * which integrates with the Stripe API for real payment processing.
 * A {@link com.ecommerce.payment.service.serviceImpl.MockPaymentProvider}
 * is available for offline testing.</p>
 */
public interface PaymentProvider {

    /**
     * Processes a payment through the provider.
     *
     * @param payment the payment entity to process
     * @return the result from the payment provider
     */
    PaymentProviderResult processPayment(Payment payment);

    /**
     * Processes a refund through the provider.
     *
     * @param refund  the refund entity to process
     * @param payment the original payment
     * @return the result from the refund processing
     */
    RefundProviderResult processRefund(Refund refund, Payment payment);

    /**
     * Result record from a payment provider call.
     *
     * @param success             whether the payment was successful
     * @param requiresAction      whether the payment requires frontend confirmation (e.g. 3D Secure)
     * @param transactionId       provider transaction/charge ID
     * @param paymentIntentId     Stripe PaymentIntent ID (null for non-Stripe providers)
     * @param clientSecret        Stripe client secret for frontend confirmation (null if auto-confirmed)
     * @param failureReason       human-readable failure description
     * @param rawResponse         raw JSON response from the provider
     */
    record PaymentProviderResult(
            boolean success,
            boolean requiresAction,
            String transactionId,
            String paymentIntentId,
            String clientSecret,
            String failureReason,
            String rawResponse
    ) {}

    /**
     * Result record from a refund provider call.
     */
    record RefundProviderResult(
            boolean success,
            String refundTransactionId,
            String failureReason
    ) {}
}
