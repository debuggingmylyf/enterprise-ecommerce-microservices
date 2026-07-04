package com.ecommerce.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Request payload for releasing a previously reserved quantity back to available stock.
 *
 * <p>Typically invoked when an order is cancelled after a reservation was made.
 */
@Getter
@Setter
public class ReleaseInventoryRequest {

    /** The product whose reservation is being released. */
    @NotNull(message = "Product ID is required")
    private UUID productId;

    /** The warehouse holding the reservation. */
    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;

    /** Quantity to release from reserved back to available; must be at least 1. */
    @Min(value = 1, message = "Quantity to release must be at least 1")
    private int quantity;
}
