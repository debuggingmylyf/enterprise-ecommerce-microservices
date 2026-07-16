package com.ecommerce.order.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Lightweight representation of inventory availability received from Inventory Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAvailabilityResponse {
    private UUID productId;
    private String warehouseCode;
    private boolean available;
    private int availableQuantity;
}
