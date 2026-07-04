package com.ecommerce.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Request payload for reserving stock against a pending order.
 *
 * <p>Reservation transitions stock from {@code available} to {@code reserved}
 * without permanently deducting it until an order is confirmed.
 */
@Getter
@Setter
public class ReserveInventoryRequest {

    /** The product for which stock is being reserved. */
    @NotNull(message = "Product ID is required")
    private UUID productId;

    /** The warehouse from which to reserve stock. */
    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;

    /** Quantity to move from available to reserved; must be at least 1. */
    @Min(value = 1, message = "Quantity to reserve must be at least 1")
    private int quantity;
}
