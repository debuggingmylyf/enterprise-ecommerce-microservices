package com.ecommerce.payment.controller;

import com.ecommerce.payment.client.OrderServiceClient;
import com.ecommerce.payment.config.StripeConfig;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.enums.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling Stripe webhook events.
 *
 * <p>Stripe sends events (e.g., {@code payment_intent.succeeded},
 * {@code payment_intent.payment_failed}) to this endpoint after
 * a payment is confirmed on the client side.</p>
 *
 * <p>This is especially important for the production flow where
 * payments are confirmed via Stripe.js on the frontend.</p>
 *
 * <p>Base path: {@code /api/v1/payments/webhook}</p>
 */
@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final StripeConfig stripeConfig;

    /**
     * Receives and processes Stripe webhook events.
     *
     * @param payload   the raw request body from Stripe
     * @param sigHeader the {@code Stripe-Signature} header for verification
     * @return {@code 200 OK} on success, {@code 400} on signature failure
     */
    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody final String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) final String sigHeader) {

        log.info("Received Stripe webhook event");

        Event event;

        // Verify webhook signature if secret is configured
        final String webhookSecret = stripeConfig.getWebhookSecret();
        if (webhookSecret != null && !webhookSecret.isBlank() && sigHeader != null) {
            try {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } catch (final SignatureVerificationException e) {
                log.error("Stripe webhook signature verification failed: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }
        } else {
            // In test mode without webhook secret, parse event directly
            event = Event.GSON.fromJson(payload, Event.class);
        }

        final String eventType = event.getType();
        log.info("Processing Stripe event: {} (id: {})", eventType, event.getId());

        switch (eventType) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
            default -> log.debug("Unhandled Stripe event type: {}", eventType);
        }

        return ResponseEntity.ok("Webhook processed");
    }

    private void handlePaymentIntentSucceeded(final Event event) {
        final PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (paymentIntent == null) {
            log.error("Failed to deserialize PaymentIntent from webhook event");
            return;
        }

        log.info("PaymentIntent succeeded: {}", paymentIntent.getId());

        final Optional<Payment> paymentOpt = paymentRepository.findAll().stream()
                .filter(p -> paymentIntent.getId().equals(p.getStripePaymentIntentId()))
                .findFirst();

        if (paymentOpt.isEmpty()) {
            log.warn("No payment found for PaymentIntent: {}", paymentIntent.getId());
            return;
        }

        final Payment payment = paymentOpt.get();

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment {} already marked as SUCCESS, skipping", payment.getId());
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(paymentIntent.getLatestCharge());
        paymentRepository.save(payment);

        // Notify order-service
        try {
            orderServiceClient.markPaymentSuccess(payment.getOrderId());
            log.info("Order {} notified of payment success via webhook", payment.getOrderId());
        } catch (final Exception ex) {
            log.error("Failed to notify order service of payment success for order {}: {}",
                    payment.getOrderId(), ex.getMessage());
        }
    }

    private void handlePaymentIntentFailed(final Event event) {
        final PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (paymentIntent == null) {
            log.error("Failed to deserialize PaymentIntent from webhook event");
            return;
        }

        log.info("PaymentIntent failed: {}", paymentIntent.getId());

        final Optional<Payment> paymentOpt = paymentRepository.findAll().stream()
                .filter(p -> paymentIntent.getId().equals(p.getStripePaymentIntentId()))
                .findFirst();

        if (paymentOpt.isEmpty()) {
            log.warn("No payment found for PaymentIntent: {}", paymentIntent.getId());
            return;
        }

        final Payment payment = paymentOpt.get();

        payment.setStatus(PaymentStatus.FAILED);
        final String failureMessage = paymentIntent.getLastPaymentError() != null
                ? paymentIntent.getLastPaymentError().getMessage()
                : "Payment failed";
        payment.setFailureReason(failureMessage);
        paymentRepository.save(payment);

        // Notify order-service
        try {
            orderServiceClient.markPaymentFailed(payment.getOrderId());
            log.info("Order {} notified of payment failure via webhook", payment.getOrderId());
        } catch (final Exception ex) {
            log.error("Failed to notify order service of payment failure for order {}: {}",
                    payment.getOrderId(), ex.getMessage());
        }
    }
}
