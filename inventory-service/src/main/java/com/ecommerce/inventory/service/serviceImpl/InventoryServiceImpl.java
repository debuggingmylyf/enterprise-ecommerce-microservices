package com.ecommerce.inventory.service.serviceImpl;

import com.ecommerce.inventory.dto.request.AdjustInventoryRequest;
import com.ecommerce.inventory.dto.request.ConfirmInventoryRequest;
import com.ecommerce.inventory.dto.request.CreateInventoryRequest;
import com.ecommerce.inventory.dto.request.ReleaseInventoryRequest;
import com.ecommerce.inventory.dto.request.ReserveInventoryRequest;
import com.ecommerce.inventory.dto.response.CreateInventoryResponse;
import com.ecommerce.inventory.dto.response.InventoryAvailabilityResponse;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.enums.StockAdjustmentType;
import com.ecommerce.inventory.exception.BusinessException;
import com.ecommerce.inventory.exception.ErrorCode;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.mapper.InventoryMapper;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link InventoryService}.
 *
 * <p>All stock-mutation operations are wrapped in transactions. Optimistic locking
 * via {@code @Version} on the {@link Inventory} entity prevents concurrent
 * double-reservation races at the JPA level.
 *
 * <p><strong>Business rules enforced:</strong>
 * <ul>
 *   <li>Reserve: {@code availableQuantity >= requestedQuantity}</li>
 *   <li>Release: {@code reservedQuantity >= requestedQuantity}</li>
 *   <li>Confirm: {@code reservedQuantity >= requestedQuantity}</li>
 *   <li>Adjust DECREASE: {@code availableQuantity >= requestedQuantity}</li>
 *   <li>Stock quantities never go negative.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    /**
     * {@inheritDoc}
     *
     * @throws BusinessException ({@link ErrorCode#DUPLICATE_INVENTORY}) if a record
     *                           already exists for this product-warehouse pair
     */
    @Override
    @Transactional
    public CreateInventoryResponse createInventory(final CreateInventoryRequest request) {
        if (inventoryRepository.existsByProductIdAndWarehouseCode(
                request.getProductId(), request.getWarehouseCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_INVENTORY,
                    "Inventory already exists for product " + request.getProductId()
                            + " in warehouse '" + request.getWarehouseCode() + "'");
        }

        final Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .warehouseCode(request.getWarehouseCode())
                .availableQuantity(request.getInitialQuantity())
                .reservedQuantity(0)
                .lowStockThreshold(request.getLowStockThreshold())
                .active(true)
                .build();

        final Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory created: id={}, productId={}, warehouse={}, qty={}",
                saved.getId(), saved.getProductId(), saved.getWarehouseCode(),
                saved.getAvailableQuantity());

        return CreateInventoryResponse.builder()
                .inventoryId(saved.getId())
                .productId(saved.getProductId())
                .warehouseCode(saved.getWarehouseCode())
                .message("Inventory record created successfully")
                .build();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Moves {@code quantity} units from {@code available} to {@code reserved}.
     *
     * @throws ResourceNotFoundException if no inventory record exists
     * @throws BusinessException         ({@link ErrorCode#INSUFFICIENT_STOCK}) if available
     *                                   stock is less than the requested quantity
     */
    @Override
    @Transactional
    public InventoryResponse reserveStock(final ReserveInventoryRequest request) {
        final Inventory inventory = resolveInventory(request.getProductId(), request.getWarehouseCode());

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                    "Insufficient stock for product " + request.getProductId()
                            + " in warehouse '" + request.getWarehouseCode()
                            + "'. Requested: " + request.getQuantity()
                            + ", Available: " + inventory.getAvailableQuantity());
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity());

        final Inventory saved = inventoryRepository.save(inventory);
        log.info("Stock reserved: productId={}, warehouse={}, qty={}, remaining={}",
                request.getProductId(), request.getWarehouseCode(),
                request.getQuantity(), saved.getAvailableQuantity());

        return inventoryMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Moves {@code quantity} units from {@code reserved} back to {@code available}.
     *
     * @throws ResourceNotFoundException if no inventory record exists
     * @throws BusinessException         ({@link ErrorCode#INVALID_QUANTITY}) if reserved
     *                                   stock is less than the quantity to release
     */
    @Override
    @Transactional
    public InventoryResponse releaseStock(final ReleaseInventoryRequest request) {
        final Inventory inventory = resolveInventory(request.getProductId(), request.getWarehouseCode());

        if (inventory.getReservedQuantity() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY,
                    "Cannot release " + request.getQuantity() + " units; only "
                            + inventory.getReservedQuantity() + " are reserved for product "
                            + request.getProductId());
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.getQuantity());
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());

        final Inventory saved = inventoryRepository.save(inventory);
        log.info("Stock released: productId={}, warehouse={}, qty={}",
                request.getProductId(), request.getWarehouseCode(), request.getQuantity());

        return inventoryMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Permanently deducts {@code quantity} from {@code reserved} (order confirmed).
     *
     * @throws ResourceNotFoundException if no inventory record exists
     * @throws BusinessException         ({@link ErrorCode#INVALID_QUANTITY}) if reserved
     *                                   stock is less than the quantity to confirm
     */
    @Override
    @Transactional
    public InventoryResponse confirmStockDeduction(final ConfirmInventoryRequest request) {
        final Inventory inventory = resolveInventory(request.getProductId(), request.getWarehouseCode());

        if (inventory.getReservedQuantity() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY,
                    "Cannot confirm " + request.getQuantity() + " units; only "
                            + inventory.getReservedQuantity() + " are reserved for product "
                            + request.getProductId());
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.getQuantity());

        final Inventory saved = inventoryRepository.save(inventory);
        log.info("Stock confirmed (deducted): productId={}, warehouse={}, qty={}",
                request.getProductId(), request.getWarehouseCode(), request.getQuantity());

        return inventoryMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>For {@code INCREASE}: adds {@code quantity} to {@code available}.
     * For {@code DECREASE}: removes {@code quantity} from {@code available}.
     *
     * @throws ResourceNotFoundException if no inventory record exists
     * @throws BusinessException         ({@link ErrorCode#INSUFFICIENT_STOCK}) if a DECREASE
     *                                   would make available quantity negative
     */
    @Override
    @Transactional
    public InventoryResponse adjustStock(final AdjustInventoryRequest request) {
        final Inventory inventory = resolveInventory(request.getProductId(), request.getWarehouseCode());

        if (request.getAdjustmentType() == StockAdjustmentType.INCREASE) {
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());
        } else {
            if (inventory.getAvailableQuantity() < request.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                        "Cannot decrease stock by " + request.getQuantity() + " units; only "
                                + inventory.getAvailableQuantity() + " available for product "
                                + request.getProductId());
            }
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());
        }

        final Inventory saved = inventoryRepository.save(inventory);
        log.info("Stock adjusted [{}]: productId={}, warehouse={}, qty={}, reason='{}', newAvailable={}",
                request.getAdjustmentType(), request.getProductId(), request.getWarehouseCode(),
                request.getQuantity(), request.getAdjustmentReason(), saved.getAvailableQuantity());

        return inventoryMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no inventory record exists for the given pair
     */
    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(final UUID productId, final String warehouseCode) {
        return inventoryMapper.toResponse(resolveInventory(productId, warehouseCode));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Returns the first active record found for the product across all warehouses.
     *
     * @throws ResourceNotFoundException if no inventory record exists for this product
     */
    @Override
    @Transactional(readOnly = true)
    public InventoryAvailabilityResponse checkAvailability(final UUID productId) {
        final List<Inventory> records = inventoryRepository.findAllByProductId(productId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No inventory found for product: " + productId);
        }

        // Return the record with the highest available quantity (best fulfilment candidate)
        final Inventory best = records.stream()
                .filter(Inventory::isActive)
                .max((a, b) -> Integer.compare(a.getAvailableQuantity(), b.getAvailableQuantity()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active inventory found for product: " + productId));

        return inventoryMapper.toAvailabilityResponse(best);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockProducts() {
        return inventoryRepository.findAllLowStock()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException                   if no inventory record exists
     * @throws BusinessException ({@link ErrorCode#INVALID_QUANTITY}) if threshold is negative
     */
    @Override
    @Transactional
    public InventoryResponse updateLowStockThreshold(
            final UUID productId, final String warehouseCode, final int threshold) {

        if (threshold < 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY,
                    "Low-stock threshold cannot be negative");
        }

        final Inventory inventory = resolveInventory(productId, warehouseCode);
        inventory.setLowStockThreshold(threshold);
        final Inventory saved = inventoryRepository.save(inventory);
        log.info("Low-stock threshold updated: productId={}, warehouse={}, threshold={}",
                productId, warehouseCode, threshold);

        return inventoryMapper.toResponse(saved);
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    /**
     * Resolves an active inventory record or throws {@link ResourceNotFoundException}.
     *
     * @param productId     the product UUID
     * @param warehouseCode the warehouse identifier
     * @return the found {@link Inventory} entity; never {@code null}
     */
    private Inventory resolveInventory(final UUID productId, final String warehouseCode) {
        return inventoryRepository.findByProductIdAndWarehouseCode(productId, warehouseCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product " + productId
                                + " in warehouse '" + warehouseCode + "'"));
    }
}
