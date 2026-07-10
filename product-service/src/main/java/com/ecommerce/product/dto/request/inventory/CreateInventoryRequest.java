package com.ecommerce.product.dto.request.inventory;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateInventoryRequest {

    private UUID productId;

    private Integer availableQuantity;

    private Integer lowStockThreshold;

    private String warehouseCode;

}