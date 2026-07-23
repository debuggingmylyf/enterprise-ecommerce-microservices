package com.ecommerce.payment.service.serviceImpl;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.Refund;
import com.ecommerce.payment.enums.PaymentMethod;
import com.ecommerce.payment.service.PaymentProvider;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Stripe-based payment provider using the Stripe Java SDK.
 *
 * <p>Supports four payment methods:
 * <ul>
 *     <li><b>CARD</b> — Stripe PaymentIntent with {@code card} payment method type</li>
 *     <li><b>UPI</b> — Stripe PaymentIntent with {@code upi} payment method type (India)</li>
 *     <li><b>WALLET</b> — Stripe PaymentIntent with {@code card} type + wallet metadata</li>
 *     <li><b>COD</b> — No Stripe call; auto-succeeds locally</li>
 * </ul>
 *
 * <p>In <b>test mode</b> ({@code stripe.auto-confirm=true}), payments are
 * automatically confirmed server-side using Stripe test payment methods
 * (e.g. {@code pm_card_visa}). In <b>production mode</b>, a {@code clientSecret}
 * is returned for frontend confirmation via Stripe.js.</p>
 *
 * <p>This is the <b>default</b> provider. To use the mock provider instead,
 * activate the {@code mock-payments} Spring profile.</p>
 */
@Component
@Profile("!mock-payments")
@Slf4j
public class StripePaymentProvider implements PaymentProvider {

    @Value("${stripe.auto-confirm:true}")
    private boolean autoConfirm;

    /**
     * Test payment methods used when {@code stripe.auto-confirm=true}.
     */
    private static final Map<PaymentMethod, String> TEST_PAYMENT_METHODS = Map.of(
            PaymentMethod.CARD, "pm_card_visa",
            PaymentMethod.UPI, "pm_card_visa",     // UPI test fallback to card in test mode
            PaymentMethod.WALLET, "pm_card_visa"   // Wallet test fallback to card in test mode
    );

    @Override
    public PaymentProviderResult processPayment(final Payment payment) {
        // COD — no Stripe call, auto-succeed locally
        if (payment.getPaymentMethod() == PaymentMethod.COD) {
            return processCodPayment(payment);
        }

        return processStripePayment(payment);
    }

    @Override
    public RefundProviderResult processRefund(final Refund refund, final Payment payment) {
        if (payment.getPaymentMethod() == PaymentMethod.COD) {
            log.info("COD refund for payment {} — auto-succeeding", payment.getId());
            return new RefundProviderResult(true, "COD-RFND-" + payment.getId().toString().substring(0, 8), null);
        }

        return processStripeRefund(refund, payment);
    }

    // ─────────────────────────────────────────────────────────────
    // COD handling
    // ─────────────────────────────────────────────────────────────

    private PaymentProviderResult processCodPayment(final Payment payment) {
        log.info("COD payment for order {} — auto-succeeding (payment deferred to delivery)", payment.getOrderId());
        final String rawResponse = "{\"provider\":\"COD\",\"status\":\"accepted\",\"note\":\"Payment will be collected on delivery\"}";

        return new PaymentProviderResult(
                true,
                false,
                "COD-" + payment.getOrderId().toString().substring(0, 8).toUpperCase(),
                null,   // no Stripe PaymentIntent
                null,   // no client secret
                null,
                rawResponse
        );
    }

    // ─────────────────────────────────────────────────────────────
    // Stripe payment processing
    // ─────────────────────────────────────────────────────────────

    private PaymentProviderResult processStripePayment(final Payment payment) {
        log.info("Stripe: Creating PaymentIntent for order {} amount {} {} method {}",
                payment.getOrderId(), payment.getAmount(), payment.getCurrency(), payment.getPaymentMethod());

        try {
            // Convert amount to smallest currency unit (paise for INR, cents for USD)
            final long amountInSmallestUnit = payment.getAmount()
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .longValue();

            final PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInSmallestUnit)
                    .setCurrency(payment.getCurrency().toLowerCase())
                    .addPaymentMethodType(mapPaymentMethodType(payment.getPaymentMethod()))
                    .putMetadata("order_id", payment.getOrderId().toString())
                    .putMetadata("user_id", payment.getUserId().toString())
                    .putMetadata("payment_method", payment.getPaymentMethod().name())
                    .putMetadata("idempotency_key", payment.getIdempotencyKey())
                    .setDescription("Payment for order " + payment.getOrderId());

            // In test/auto-confirm mode, attach a test payment method and confirm server-side
            if (autoConfirm) {
                final String testPm = TEST_PAYMENT_METHODS.getOrDefault(payment.getPaymentMethod(), "pm_card_visa");
                paramsBuilder
                        .setPaymentMethod(testPm)
                        .setConfirm(true)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                        .build()
                        );
            }

            final PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

            log.info("Stripe PaymentIntent created: id={}, status={}", paymentIntent.getId(), paymentIntent.getStatus());

            return mapStripeResult(paymentIntent);

        } catch (final StripeException e) {
            log.error("Stripe API error for order {}: {} (code: {})",
                    payment.getOrderId(), e.getMessage(), e.getCode());

            return new PaymentProviderResult(
                    false,
                    false,
                    null,
                    null,
                    null,
                    e.getMessage(),
                    e.getStripeError() != null ? e.getStripeError().toJson() : e.getMessage()
            );
        }
    }

    private PaymentProviderResult mapStripeResult(final PaymentIntent paymentIntent) {
        final String status = paymentIntent.getStatus();

        return switch (status) {
            case "succeeded" -> new PaymentProviderResult(
                    true,
                    false,
                    paymentIntent.getLatestCharge(),
                    paymentIntent.getId(),
                    null,   // no client secret needed — already confirmed
                    null,
                    paymentIntent.toJson()
            );
            case "requires_action", "requires_confirmation", "requires_payment_method" -> new PaymentProviderResult(
                    false,
                    true,   // requires frontend action (3D Secure, etc.)
                    null,
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret(),
                    "Payment requires additional confirmation",
                    paymentIntent.toJson()
            );
            case "processing" -> new PaymentProviderResult(
                    false,
                    true,
                    null,
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret(),
                    "Payment is still processing",
                    paymentIntent.toJson()
            );
            default -> new PaymentProviderResult(
                    false,
                    false,
                    null,
                    paymentIntent.getId(),
                    null,
                    "Payment failed with status: " + status,
                    paymentIntent.toJson()
            );
        };
    }

    // ─────────────────────────────────────────────────────────────
    // Stripe refund processing
    // ─────────────────────────────────────────────────────────────

    private RefundProviderResult processStripeRefund(final Refund refund, final Payment payment) {
        log.info("Stripe: Processing refund of {} for PaymentIntent {}",
                refund.getAmount(), payment.getStripePaymentIntentId());

        try {
            final long amountInSmallestUnit = refund.getAmount()
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .longValue();

            final RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getStripePaymentIntentId())
                    .setAmount(amountInSmallestUnit)
                    .putMetadata("reason", refund.getReason())
                    .putMetadata("payment_id", payment.getId().toString())
                    .build();

            final com.stripe.model.Refund stripeRefund = com.stripe.model.Refund.create(params);

            log.info("Stripe refund created: id={}, status={}", stripeRefund.getId(), stripeRefund.getStatus());

            final boolean success = "succeeded".equals(stripeRefund.getStatus());
            return new RefundProviderResult(
                    success,
                    stripeRefund.getId(),
                    success ? null : "Refund status: " + stripeRefund.getStatus()
            );

        } catch (final StripeException e) {
            log.error("Stripe refund error for payment {}: {}", payment.getId(), e.getMessage());
            return new RefundProviderResult(false, null, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    /**
     * Maps internal PaymentMethod enum to Stripe payment_method_type string.
     */
    private String mapPaymentMethodType(final PaymentMethod method) {
        return switch (method) {
            case CARD -> "card";
            case UPI -> "upi";
            case WALLET -> "card";    // wallets use card rails in Stripe
            case COD -> throw new IllegalArgumentException("COD should not reach Stripe");
        };
    }
}
