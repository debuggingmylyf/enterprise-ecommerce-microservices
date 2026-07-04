package com.ecommerce.inventory.dto.request;

import com.ecommerce.inventory.enums.StockAdjustmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Request payload for performing a manual stock adjustment (admin operation).
 *
 * <p>Adjustments cover operational scenarios such as goods received, damaged stock,
 * shrinkage, or periodic cycle-count corrections.
 */
@Getter
@Setter
public class AdjustInventoryRequest {

    /** The product whose stock is being adjusted. */
    @NotNull(message = "Product ID is required")
    private UUID productId;

    /** The warehouse where the adjustment is applied. */
    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;

    /** The quantity delta; must be at least 1. */
    @Min(value = 1, message = "Adjustment quantity must be at least 1")
    private int quantity;

    /**
     * Direction of the adjustment: {@code INCREASE} adds stock,
     * {@code DECREASE} removes stock.
     */
    @NotNull(message = "Adjustment type (INCREASE or DECREASE) is required")
    private StockAdjustmentType adjustmentType;

    /**
     * Human-readable reason for this adjustment (for audit trail).
     * Examples: "Goods received from supplier", "Damaged in transit", "Cycle count correction".
     */
    @NotBlank(message = "Adjustment reason is required for audit purposes")
    private String adjustmentReason;
}
