package com.ecommerce.order.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Payload to request confirming inventory deduction in Inventory Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmInventoryRequest {
    private UUID productId;
    private String warehouseCode;
    private int quantity;
}
