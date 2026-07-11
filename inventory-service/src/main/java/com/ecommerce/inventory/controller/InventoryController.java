package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.request.AdjustInventoryRequest;
import com.ecommerce.inventory.dto.request.ConfirmInventoryRequest;
import com.ecommerce.inventory.dto.request.CreateInventoryRequest;
import com.ecommerce.inventory.dto.request.ReleaseInventoryRequest;
import com.ecommerce.inventory.dto.request.ReserveInventoryRequest;
import com.ecommerce.inventory.dto.response.CreateInventoryResponse;
import com.ecommerce.inventory.dto.response.InventoryAvailabilityResponse;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing all Inventory Service endpoints.
 *
 * <p>
 * Base path: {@code /api/v1/inventory}
 *
 * <p>
 * <strong>Endpoint groups:</strong>
 * <ul>
 * <li><em>Admin</em>: create, adjust, update threshold — require ADMIN role
 * (enforced at gateway).</li>
 * <li><em>Internal</em>: reserve, release, confirm — require INTERNAL_SERVICE
 * role (enforced at gateway).</li>
 * <li><em>Read</em>: get, check, low-stock — publicly readable through the
 * gateway.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

        private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

        private final InventoryService inventoryService;

        private static String sanitize(final String value) {
                return value == null ? null : value.replaceAll("[\n\r]", "_");
        }

        // -----------------------------------------------------------------------
        // Admin Endpoints
        // -----------------------------------------------------------------------

        /**
         * Creates a new inventory record for a product in a warehouse.
         *
         * <p>
         * <strong>Role required:</strong> ADMIN
         *
         * @param request the validated creation payload
         * @return {@code 201 Created} with a lightweight
         *         {@link CreateInventoryResponse}
         */
        @PostMapping
        public ResponseEntity<CreateInventoryResponse> createInventory(
                        @Valid @RequestBody final CreateInventoryRequest request) {

                log.info("POST /api/v1/inventory – productId={}, warehouse={}",
                                request.getProductId(), sanitize(request.getWarehouseCode()));
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(inventoryService.createInventory(request));
        }

        /**
         * Performs a manual stock adjustment (increase or decrease).
         *
         * <p>
         * <strong>Role required:</strong> ADMIN
         *
         * @param productId the UUID of the product to adjust
         * @param request   the adjustment payload including type and audit reason
         * @return {@code 200 OK} with the updated {@link InventoryResponse}
         */
        @PatchMapping("/{productId}/adjust")
        public ResponseEntity<InventoryResponse> adjustStock(
                        @PathVariable final UUID productId,
                        @Valid @RequestBody final AdjustInventoryRequest request) {

                log.info("PATCH /api/v1/inventory/{}/adjust – type={}, qty={}",
                                productId, request.getAdjustmentType(), request.getQuantity());
                request.setProductId(productId);
                return ResponseEntity.ok(inventoryService.adjustStock(request));
        }

        /**
         * Updates the low-stock threshold for a product's inventory in a specific
         * warehouse.
         *
         * <p>
         * <strong>Role required:</strong> ADMIN
         *
         * @param productId     the UUID of the product
         * @param warehouseCode the warehouse identifier (query param)
         * @param threshold     the new low-stock threshold value
         * @return {@code 200 OK} with the updated {@link InventoryResponse}
         */
        @PatchMapping("/{productId}/threshold")
        public ResponseEntity<InventoryResponse> updateLowStockThreshold(
                        @PathVariable final UUID productId,
                        @RequestParam final String warehouseCode,
                        @RequestParam @Min(value = 0, message = "Threshold must be zero or greater") final int threshold) {

                log.info("PATCH /api/v1/inventory/{}/threshold – warehouse={}, threshold={}",
                                productId, sanitize(warehouseCode), threshold);
                return ResponseEntity.ok(
                                inventoryService.updateLowStockThreshold(productId, warehouseCode, threshold));
        }

        // -----------------------------------------------------------------------
        // Internal Endpoints (service-to-service)
        // -----------------------------------------------------------------------

        /**
         * Provisions a new inventory record for a product, called internally by
         * other services (e.g. product-service upon product creation).
         *
         * <p>
         * <strong>Role required:</strong> INTERNAL_SERVICE (enforced at gateway)
         *
         * <p>
         * This endpoint is intentionally separate from the ADMIN
         * {@code POST /api/v1/inventory} endpoint so that the API gateway can
         * apply different role policies without mixing concerns.
         *
         * @param request the validated creation payload
         * @return {@code 201 Created} with a lightweight
         *         {@link CreateInventoryResponse}
         */
        @PostMapping("/internal/provision")
        public ResponseEntity<CreateInventoryResponse> provisionInventory(
                        @Valid @RequestBody final CreateInventoryRequest request) {

                log.info("POST /api/v1/inventory/internal/provision – productId={}, warehouse={}",
                                request.getProductId(), sanitize(request.getWarehouseCode()));
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(inventoryService.createInventory(request));
        }

        /**
         * Reserves stock for a pending order.
         *
         * <p>
         * <strong>Role required:</strong> INTERNAL_SERVICE
         *
         * @param request the reservation payload
         * @return {@code 200 OK} with the updated {@link InventoryResponse}
         */
        @PatchMapping("/reserve")
        public ResponseEntity<InventoryResponse> reserveStock(
                        @Valid @RequestBody final ReserveInventoryRequest request) {

                log.info("PATCH /api/v1/inventory/reserve – productId={}, qty={}",
                                request.getProductId(), request.getQuantity());
                return ResponseEntity.ok(inventoryService.reserveStock(request));
        }

        /**
         * Releases a reservation back to available stock (e.g. order cancelled).
         *
         * <p>
         * <strong>Role required:</strong> INTERNAL_SERVICE
         *
         * @param request the release payload
         * @return {@code 200 OK} with the updated {@link InventoryResponse}
         */
        @PatchMapping("/release")
        public ResponseEntity<InventoryResponse> releaseStock(
                        @Valid @RequestBody final ReleaseInventoryRequest request) {

                log.info("PATCH /api/v1/inventory/release – productId={}, qty={}",
                                request.getProductId(), request.getQuantity());
                return ResponseEntity.ok(inventoryService.releaseStock(request));
        }

        /**
         * Confirms a reservation (order finalised); permanently deducts from reserved.
         *
         * <p>
         * <strong>Role required:</strong> INTERNAL_SERVICE
         *
         * @param request the confirmation payload
         * @return {@code 200 OK} with the updated {@link InventoryResponse}
         */
        @PatchMapping("/confirm")
        public ResponseEntity<InventoryResponse> confirmStock(
                        @Valid @RequestBody final ConfirmInventoryRequest request) {

                log.info("PATCH /api/v1/inventory/confirm – productId={}, qty={}",
                                request.getProductId(), request.getQuantity());
                return ResponseEntity.ok(inventoryService.confirmStockDeduction(request));
        }

        // -----------------------------------------------------------------------
        // Read Endpoints
        // -----------------------------------------------------------------------

        /**
         * Returns the full inventory record for a product in a specific warehouse.
         *
         * @param productId     the UUID of the product
         * @param warehouseCode the warehouse identifier (query param)
         * @return {@code 200 OK} with the {@link InventoryResponse}
         */
        @GetMapping("/{productId}")
        public ResponseEntity<InventoryResponse> getInventory(
                        @PathVariable final UUID productId,
                        @RequestParam final String warehouseCode) {

                log.debug("GET /api/v1/inventory/{} – warehouse={}", productId, sanitize(warehouseCode));
                return ResponseEntity.ok(
                                inventoryService.getInventoryByProductId(productId, warehouseCode));
        }

        /**
         * Returns a lightweight availability check for the best warehouse for a
         * product.
         *
         * @param productId the UUID of the product
         * @return {@code 200 OK} with the {@link InventoryAvailabilityResponse}
         */
        @GetMapping("/check/{productId}")
        public ResponseEntity<InventoryAvailabilityResponse> checkAvailability(
                        @PathVariable final UUID productId) {

                log.debug("GET /api/v1/inventory/check/{}", productId);
                return ResponseEntity.ok(inventoryService.checkAvailability(productId));
        }

        /**
         * Returns all active inventory records currently below their low-stock
         * threshold.
         *
         * @return {@code 200 OK} with a list of low-stock {@link InventoryResponse}
         *         records
         */
        @GetMapping("/low-stock")
        public ResponseEntity<List<InventoryResponse>> getLowStockProducts() {
                log.debug("GET /api/v1/inventory/low-stock");
                return ResponseEntity.ok(inventoryService.getLowStockProducts());
        }
}
