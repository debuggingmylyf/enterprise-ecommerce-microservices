-- ============================================================
-- Payment Service — Initial Schema Migration
-- Stripe-integrated payment and refund tables
-- ============================================================

CREATE TABLE payments (
    id                        UUID PRIMARY KEY,
    order_id                  UUID NOT NULL,
    user_id                   UUID NOT NULL,
    amount                    NUMERIC(12,2) NOT NULL,
    currency                  VARCHAR(3) NOT NULL DEFAULT 'INR',
    payment_method            VARCHAR(20) NOT NULL,
    status                    VARCHAR(30) NOT NULL,
    transaction_id            VARCHAR(100) UNIQUE,
    failure_reason            VARCHAR(500),
    idempotency_key           VARCHAR(100) NOT NULL UNIQUE,
    provider_response         TEXT,
    stripe_payment_intent_id  VARCHAR(100) UNIQUE,
    client_secret             VARCHAR(255),
    created_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                VARCHAR(255),
    updated_by                VARCHAR(255),
    version                   BIGINT DEFAULT 0
);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_idempotency_key ON payments(idempotency_key);
CREATE INDEX idx_payments_stripe_pi_id ON payments(stripe_payment_intent_id);

CREATE TABLE refunds (
    id                        UUID PRIMARY KEY,
    payment_id                UUID NOT NULL REFERENCES payments(id),
    amount                    NUMERIC(12,2) NOT NULL,
    status                    VARCHAR(30) NOT NULL,
    reason                    VARCHAR(500) NOT NULL,
    refund_transaction_id     VARCHAR(100),
    created_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                VARCHAR(255),
    updated_by                VARCHAR(255),
    version                   BIGINT DEFAULT 0
);

CREATE INDEX idx_refunds_payment_id ON refunds(payment_id);
