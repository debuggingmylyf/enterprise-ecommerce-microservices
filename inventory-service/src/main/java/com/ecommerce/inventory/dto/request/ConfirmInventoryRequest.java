package com.ecommerce.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Request payload for confirming a stock deduction after an order is finalised.
 *
 * <p>Confirm permanently removes the quantity from {@code reserved},
 * completing the reservation lifecycle: reserve → confirm.
 */
@Getter
@Setter
public class ConfirmInventoryRequest {

    /** The product whose reserved stock is being confirmed/deducted. */
    @NotNull(message = "Product ID is required")
    private UUID productId;

    /** The warehouse holding the reservation. */
    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;

    /** Quantity to permanently deduct from reserved; must be at least 1. */
    @Min(value = 1, message = "Quantity to confirm must be at least 1")
    private int quantity;
}
