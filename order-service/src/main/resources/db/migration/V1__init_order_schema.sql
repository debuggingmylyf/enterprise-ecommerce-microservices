-- V1__init_order_schema.sql
-- Initial schema for the Order Service.

CREATE TABLE orders (
    id                      UUID            PRIMARY KEY,
    order_number            VARCHAR(100)    NOT NULL UNIQUE,
    user_id                 UUID            NOT NULL,
    total_amount            DECIMAL(12,2)   NOT NULL,
    status                  VARCHAR(30)     NOT NULL,
    payment_status          VARCHAR(30)     NOT NULL,
    shipping_name           VARCHAR(100)    NOT NULL,
    shipping_phone          VARCHAR(20)     NOT NULL,
    shipping_address_line1  VARCHAR(255)    NOT NULL,
    shipping_address_line2  VARCHAR(255),
    city                    VARCHAR(100)    NOT NULL,
    state                   VARCHAR(100)    NOT NULL,
    country                 VARCHAR(100)    NOT NULL,
    postal_code             VARCHAR(20)     NOT NULL,
    version                 BIGINT          NOT NULL DEFAULT 0,
    created_at              TIMESTAMP       NOT NULL,
    updated_at              TIMESTAMP       NOT NULL,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),

    CONSTRAINT chk_total_amount_non_negative
        CHECK (total_amount >= 0)
);

CREATE TABLE order_items (
    id              UUID            PRIMARY KEY,
    order_id        UUID            NOT NULL,
    product_id      UUID            NOT NULL,
    warehouse_code  VARCHAR(50)     NOT NULL,
    quantity        INTEGER         NOT NULL,
    unit_price      DECIMAL(12,2)   NOT NULL,
    subtotal        DECIMAL(12,2)   NOT NULL,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_quantity_positive
        CHECK (quantity > 0),
    CONSTRAINT chk_unit_price_non_negative
        CHECK (unit_price >= 0),
    CONSTRAINT chk_subtotal_non_negative
        CHECK (subtotal >= 0)
);

-- Indexes for performance
CREATE INDEX idx_orders_order_number
    ON orders (order_number);

CREATE INDEX idx_orders_user_id
    ON orders (user_id);

CREATE INDEX idx_orders_status
    ON orders (status);
