package com.ecommerce.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Lightweight availability check response for the internal stock-check endpoint.
 *
 * <p>Returned by {@code GET /inventory/check/{productId}} so that other services
 * (e.g. Order Service) can quickly determine stock availability without receiving
 * the full inventory record.
 */
@Getter
@Setter
@Builder
public class InventoryAvailabilityResponse {

    private UUID productId;

    private String warehouseCode;

    /** {@code true} if sufficient stock is available for at least 1 unit. */
    private boolean available;

    /** Current available quantity. */
    private int availableQuantity;
}
