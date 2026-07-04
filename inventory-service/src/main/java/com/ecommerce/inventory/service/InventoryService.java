package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.request.AdjustInventoryRequest;
import com.ecommerce.inventory.dto.request.ConfirmInventoryRequest;
import com.ecommerce.inventory.dto.request.CreateInventoryRequest;
import com.ecommerce.inventory.dto.request.ReleaseInventoryRequest;
import com.ecommerce.inventory.dto.request.ReserveInventoryRequest;
import com.ecommerce.inventory.dto.response.CreateInventoryResponse;
import com.ecommerce.inventory.dto.response.InventoryAvailabilityResponse;
import com.ecommerce.inventory.dto.response.InventoryResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for all inventory operations.
 *
 * <p>Implementations must enforce the following core business rules:
 * <ul>
 *   <li>Available quantity may never go negative.</li>
 *   <li>Reserved quantity may never go negative.</li>
 *   <li>A reservation requires {@code availableQuantity >= requestedQuantity}.</li>
 *   <li>A release or confirm requires {@code reservedQuantity >= requestedQuantity}.</li>
 * </ul>
 */
public interface InventoryService {

    /**
     * Creates a new inventory record for a product in a warehouse.
     *
     * @param request the creation request
     * @return a lightweight creation confirmation
     */
    CreateInventoryResponse createInventory(CreateInventoryRequest request);

    /**
     * Reserves the requested quantity by moving it from {@code available} to {@code reserved}.
     *
     * @param request the reservation request
     * @return the updated inventory state
     */
    InventoryResponse reserveStock(ReserveInventoryRequest request);

    /**
     * Releases a previously reserved quantity back to available stock.
     *
     * @param request the release request
     * @return the updated inventory state
     */
    InventoryResponse releaseStock(ReleaseInventoryRequest request);

    /**
     * Confirms a reservation by permanently deducting the quantity from reserved.
     *
     * @param request the confirmation request
     * @return the updated inventory state
     */
    InventoryResponse confirmStockDeduction(ConfirmInventoryRequest request);

    /**
     * Performs a manual stock adjustment (admin operation).
     *
     * @param request the adjustment request including direction and audit reason
     * @return the updated inventory state
     */
    InventoryResponse adjustStock(AdjustInventoryRequest request);

    /**
     * Returns the inventory state for a specific product in a specific warehouse.
     *
     * @param productId     the product UUID
     * @param warehouseCode the warehouse identifier
     * @return the inventory response
     */
    InventoryResponse getInventoryByProductId(UUID productId, String warehouseCode);

    /**
     * Returns a lightweight availability check for a product's primary inventory.
     *
     * @param productId the product UUID
     * @return availability summary
     */
    InventoryAvailabilityResponse checkAvailability(UUID productId);

    /**
     * Returns all inventory records that are currently in a low-stock state.
     *
     * @return list of low-stock inventory responses; never {@code null}
     */
    List<InventoryResponse> getLowStockProducts();

    /**
     * Updates the low-stock threshold for a product's inventory record.
     *
     * @param productId     the product UUID
     * @param warehouseCode the warehouse identifier
     * @param threshold     the new threshold value; must be non-negative
     * @return the updated inventory state
     */
    InventoryResponse updateLowStockThreshold(UUID productId, String warehouseCode, int threshold);
}
