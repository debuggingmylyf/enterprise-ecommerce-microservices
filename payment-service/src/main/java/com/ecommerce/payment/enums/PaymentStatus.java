package com.ecommerce.payment.enums;

/**
 * Lifecycle status of a payment transaction.
 */
public enum PaymentStatus {
    INITIATED,    // Payment record created, not yet processed
    PROCESSING,   // Sent to provider, awaiting response
    SUCCESS,      // Provider confirmed payment
    FAILED,       // Provider rejected payment
    REFUNDED      // Full refund processed
}
