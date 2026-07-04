package com.ecommerce.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Request payload for creating a new inventory record for a product in a
 * warehouse.
 */
@Getter
@Setter
public class CreateInventoryRequest {

    /** The product this inventory entry belongs to. */
    @NotNull(message = "Product ID is required")
    private UUID productId;

    /** Logical warehouse identifier (e.g. "WH-DELHI"). */
    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;

    /** Initial available stock quantity; must be zero or positive. */
    @Min(value = 0, message = "Initial quantity must be zero or greater")
    private int initialQuantity;

    /**
     * Quantity at or below which this record is flagged as low-stock.
     * Defaults to 10 if not provided.
     */
    @Min(value = 0, message = "Low-stock threshold must be zero or greater")
    private int lowStockThreshold = 10;
}
