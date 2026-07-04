package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Inventory} entities.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    /**
     * Finds the inventory record for a specific product in a specific warehouse.
     *
     * @param productId     the product UUID
     * @param warehouseCode the warehouse identifier
     * @return an {@link Optional} containing the record, or empty if none found
     */
    Optional<Inventory> findByProductIdAndWarehouseCode(UUID productId, String warehouseCode);

    /**
     * Returns all inventory records for a given product across all warehouses.
     *
     * @param productId the product UUID
     * @return list of matching {@link Inventory} records; never {@code null}
     */
    List<Inventory> findAllByProductId(UUID productId);

    /**
     * Returns whether an inventory record exists for a given product-warehouse pair.
     *
     * @param productId     the product UUID
     * @param warehouseCode the warehouse identifier
     * @return {@code true} if a matching record exists
     */
    boolean existsByProductIdAndWarehouseCode(UUID productId, String warehouseCode);

    /**
     * Returns all active inventory records where available quantity has dropped
     * to or below the configured low-stock threshold.
     *
     * @return list of low-stock {@link Inventory} records; never {@code null}
     */
    @Query("SELECT i FROM Inventory i WHERE i.active = true AND i.availableQuantity <= i.lowStockThreshold")
    List<Inventory> findAllLowStock();
}
