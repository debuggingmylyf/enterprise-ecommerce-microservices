package com.ecommerce.product.dto.response.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Client-side mirror of the inventory-service {@code CreateInventoryResponse}.
 *
 * <p>Returned by the Feign client after a successful inventory provisioning call.</p>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryResponse {

    /** The newly created inventory record ID (from inventory-service). */
    private UUID inventoryId;

    /** The product this inventory record belongs to. */
    private UUID productId;

    /** Warehouse code where the stock was created. */
    private String warehouseCode;

    /** Human-readable confirmation message from inventory-service. */
    private String message;
}
