package com.ecommerce.payment.service.serviceImpl;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.Refund;
import com.ecommerce.payment.service.PaymentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock payment provider for offline development and testing.
 *
 * <p>Simulates payment processing with a configurable success rate (90%)
 * and a small random delay. Only activated when the {@code mock-payments}
 * Spring profile is active.</p>
 *
 * <p>For real Stripe integration, use the default
 * {@link StripePaymentProvider}.</p>
 */
@Component
@Profile("mock-payments")
@Slf4j
public class MockPaymentProvider implements PaymentProvider {

    private static final double SUCCESS_RATE = 0.90;
    private static final long MIN_DELAY_MS = 100;
    private static final long MAX_DELAY_MS = 300;

    @Override
    public PaymentProviderResult processPayment(final Payment payment) {
        log.info("MockPaymentProvider: Processing payment for order {} amount {} {}",
                payment.getOrderId(), payment.getAmount(), payment.getCurrency());

        simulateDelay();

        final boolean success = ThreadLocalRandom.current().nextDouble() < SUCCESS_RATE;
        final String transactionId = success ? "MOCK-TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase() : null;
        final String failureReason = success ? null : "Mock payment declined — insufficient funds simulation";
        final String rawResponse = String.format("{\"mock\":true,\"success\":%s,\"txnId\":\"%s\"}", success, transactionId);

        log.info("MockPaymentProvider: Payment {} — txnId={}", success ? "SUCCESS" : "FAILED", transactionId);

        return new PaymentProviderResult(success, false, transactionId, null, null, failureReason, rawResponse);
    }

    @Override
    public RefundProviderResult processRefund(final Refund refund, final Payment payment) {
        log.info("MockPaymentProvider: Processing refund of {} for payment {} (txn: {})",
                refund.getAmount(), payment.getId(), payment.getTransactionId());

        simulateDelay();

        final String refundTxnId = "MOCK-RFND-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        log.info("MockPaymentProvider: Refund SUCCESS — refundTxnId={}", refundTxnId);

        return new RefundProviderResult(true, refundTxnId, null);
    }

    private void simulateDelay() {
        try {
            final long delay = ThreadLocalRandom.current().nextLong(MIN_DELAY_MS, MAX_DELAY_MS);
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
