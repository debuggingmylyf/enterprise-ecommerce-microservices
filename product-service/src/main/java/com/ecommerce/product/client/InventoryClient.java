package com.ecommerce.product.client;

import com.ecommerce.product.dto.request.inventory.CreateInventoryRequest;
import com.ecommerce.product.dto.response.inventory.CreateInventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Feign client for communicating with the Inventory Service.
 *
 * <p>All methods are protected by a Resilience4j circuit breaker named
 * {@code inventoryService}, configured in {@code product-service.yml}.
 * Fallback behaviour is provided by {@link InventoryClientFallbackFactory}.</p>
 *
 * <p><strong>Service discovery:</strong> resolves {@code INVENTORY-SERVICE} via Eureka.</p>
 */
@FeignClient(
        name = "INVENTORY-SERVICE",
        fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryClient {

    /**
     * Provisions a new default inventory record for a newly created product.
     *
     * <p>Calls the internal endpoint {@code POST /api/v1/inventory/internal/provision}
     * which is protected at the gateway with the {@code INTERNAL_SERVICE} role.</p>
     *
     * @param request the inventory provisioning payload
     * @return {@code 201 Created} with the new inventory record summary
     */
    @PostMapping("/api/v1/inventory/internal/provision")
    ResponseEntity<CreateInventoryResponse> provisionInventory(
            @RequestBody CreateInventoryRequest request);

    /**
     * Checks the stock availability for a product by its UUID.
     *
     * @param productId the UUID of the product
     * @return a lightweight availability response
     */
    @GetMapping("/api/v1/inventory/check/{productId}")
    ResponseEntity<Object> checkAvailability(@PathVariable("productId") UUID productId);
}
