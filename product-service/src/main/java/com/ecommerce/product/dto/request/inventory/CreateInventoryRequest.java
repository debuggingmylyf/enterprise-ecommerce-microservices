package com.ecommerce.product.dto.request.inventory;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Request payload sent from product-service to inventory-service when provisioning
 * a new inventory record upon product creation.
 *
 * <p>Field names mirror the inventory-service {@code CreateInventoryRequest} schema.</p>
 */
@Builder
@Getter
public class CreateInventoryRequest {

    /** The product this inventory entry belongs to. */
    private UUID productId;

    /** Logical warehouse identifier (e.g. "WH-DEFAULT"). */
    private String warehouseCode;

    /** Initial available stock quantity. */
    private int initialQuantity;

    /**
     * Quantity at or below which this record is flagged as low-stock.
     * Defaults to 10 if not provided by the caller.
     */
    private int lowStockThreshold;
}