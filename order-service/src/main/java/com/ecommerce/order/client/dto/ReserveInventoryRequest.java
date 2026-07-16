package com.ecommerce.order.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Payload to request inventory reservation in Inventory Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveInventoryRequest {
    private UUID productId;
    private String warehouseCode;
    private int quantity;
}
