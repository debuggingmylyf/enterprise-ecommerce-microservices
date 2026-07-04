-- V1__init_inventory_schema.sql
-- Initial schema for the Inventory Service.
-- Creates the inventory table with indexes and constraints.

CREATE TABLE inventory (
    id                  UUID            PRIMARY KEY,
    product_id          UUID            NOT NULL,
    available_quantity  INTEGER         NOT NULL DEFAULT 0,
    reserved_quantity   INTEGER         NOT NULL DEFAULT 0,
    warehouse_code      VARCHAR(50)     NOT NULL,
    low_stock_threshold INTEGER         NOT NULL DEFAULT 10,
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    version             BIGINT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP       NOT NULL,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    -- Business rule: quantities must never be negative
    CONSTRAINT chk_available_quantity_non_negative
        CHECK (available_quantity >= 0),
    CONSTRAINT chk_reserved_quantity_non_negative
        CHECK (reserved_quantity >= 0),
    CONSTRAINT chk_low_stock_threshold_non_negative
        CHECK (low_stock_threshold >= 0),

    -- One inventory record per product per warehouse
    CONSTRAINT uq_inventory_product_warehouse
        UNIQUE (product_id, warehouse_code)
);

-- Index for fast product-level lookups
CREATE INDEX idx_inventory_product_id
    ON inventory (product_id);

-- Index for warehouse-level reporting
CREATE INDEX idx_inventory_warehouse_code
    ON inventory (warehouse_code);

-- Partial index for efficient low-stock monitoring queries
CREATE INDEX idx_inventory_low_stock
    ON inventory (available_quantity, low_stock_threshold)
    WHERE active = TRUE;
