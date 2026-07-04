package com.ecommerce.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * Represents the inventory record for a specific product in a specific warehouse.
 *
 * <p>The {@link #version} field enables JPA optimistic locking, which prevents
 * concurrent double-reservations without requiring pessimistic database locks.
 *
 * <p>A composite unique constraint on {@code (product_id, warehouse_code)} enforces
 * the rule that each product can have at most one inventory record per warehouse.
 */
@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_inventory_product_warehouse",
                        columnNames = {"product_id", "warehouse_code"}
                )
        },
        indexes = {
                @Index(name = "idx_inventory_product_id", columnList = "product_id"),
                @Index(name = "idx_inventory_warehouse_code", columnList = "warehouse_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @jakarta.persistence.Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** The product this inventory record belongs to (external reference to Product Service). */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Quantity immediately available for purchase.
     * Must never go below zero.
     */
    @Builder.Default
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity = 0;

    /**
     * Quantity currently held by pending orders but not yet confirmed/deducted.
     * Must never go below zero.
     */
    @Builder.Default
    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity = 0;

    /** Logical warehouse identifier (e.g. "WH-DELHI", "WH-MUMBAI"). */
    @Column(name = "warehouse_code", nullable = false, length = 50)
    private String warehouseCode;

    /**
     * When {@code availableQuantity} drops to or below this threshold,
     * the record is considered low-stock.
     */
    @Builder.Default
    @Column(name = "low_stock_threshold", nullable = false)
    private int lowStockThreshold = 10;

    /** Soft-delete flag. Inactive records are excluded from stock checks. */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * JPA optimistic locking version counter.
     * Incremented on every update; prevents lost-update races on concurrent reservations.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // -----------------------------------------------------------------------
    // Domain methods
    // -----------------------------------------------------------------------

    /**
     * Returns {@code true} when the available stock is at or below the low-stock threshold.
     *
     * @return {@code true} if this inventory is considered low-stock
     */
    public boolean isLowStock() {
        return availableQuantity <= lowStockThreshold;
    }
}
