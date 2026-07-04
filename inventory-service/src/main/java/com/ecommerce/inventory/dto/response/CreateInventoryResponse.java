package com.ecommerce.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Lightweight creation confirmation returned after a new inventory record is created.
 */
@Getter
@Setter
@Builder
public class CreateInventoryResponse {

    private UUID inventoryId;

    private UUID productId;

    private String warehouseCode;

    /** Human-readable confirmation message. */
    private String message;
}
