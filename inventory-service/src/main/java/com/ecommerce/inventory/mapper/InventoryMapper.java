package com.ecommerce.inventory.mapper;

import com.ecommerce.inventory.dto.response.InventoryAvailabilityResponse;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

/**
 * Spring-managed mapper converting {@link Inventory} entities into response DTOs.
 *
 * <p>Manual mapping is used intentionally, consistent with the product-service approach.
 */
@Component
public class InventoryMapper {

    /**
     * Maps an {@link Inventory} entity to a full {@link InventoryResponse} DTO.
     *
     * @param inventory the source entity; must not be {@code null}
     * @return a fully populated {@link InventoryResponse}; never {@code null}
     * @throws IllegalArgumentException if {@code inventory} is {@code null}
     */
    public InventoryResponse toResponse(final Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory must not be null");
        }

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .warehouseCode(inventory.getWarehouseCode())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .lowStock(inventory.isLowStock())
                .active(inventory.isActive())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    /**
     * Maps an {@link Inventory} entity to a lightweight {@link InventoryAvailabilityResponse}.
     *
     * @param inventory the source entity; must not be {@code null}
     * @return a populated {@link InventoryAvailabilityResponse}; never {@code null}
     * @throws IllegalArgumentException if {@code inventory} is {@code null}
     */
    public InventoryAvailabilityResponse toAvailabilityResponse(final Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory must not be null");
        }

        return InventoryAvailabilityResponse.builder()
                .productId(inventory.getProductId())
                .warehouseCode(inventory.getWarehouseCode())
                .available(inventory.getAvailableQuantity() > 0)
                .availableQuantity(inventory.getAvailableQuantity())
                .build();
    }
}
