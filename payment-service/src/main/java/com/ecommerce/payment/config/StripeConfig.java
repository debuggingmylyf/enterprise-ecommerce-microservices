package com.ecommerce.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe SDK configuration.
 *
 * <p>Initialises the Stripe global API key from the
 * {@code stripe.secret-key} property (resolved from the
 * {@code STRIPE_SECRET_KEY} environment variable).</p>
 *
 * <p>For testing, use a Stripe <b>test</b> secret key
 * (starts with {@code sk_test_}).</p>
 */
@Configuration
@Slf4j
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        final boolean isTestMode = secretKey.startsWith("sk_test_");
        log.info("Stripe SDK initialized — test mode: {}", isTestMode);
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
}
