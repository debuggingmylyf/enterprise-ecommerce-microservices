package com.ecommerce.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Full inventory record projection returned for admin reads and mutation responses.
 *
 * <p>{@code lowStock} is a computed convenience flag; consumers can use it
 * directly without re-evaluating {@code availableQuantity} vs {@code lowStockThreshold}.
 */
@Getter
@Setter
@Builder
public class InventoryResponse {

    private UUID id;

    private UUID productId;

    private String warehouseCode;

    /** Quantity immediately purchasable. */
    private int availableQuantity;

    /** Quantity held by pending orders. */
    private int reservedQuantity;

    /** Threshold at or below which low-stock alerts apply. */
    private int lowStockThreshold;

    /**
     * Computed flag: {@code true} when {@code availableQuantity <= lowStockThreshold}.
     */
    private boolean lowStock;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
