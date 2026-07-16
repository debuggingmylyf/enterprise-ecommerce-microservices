package com.ecommerce.order.client;

import com.ecommerce.order.client.dto.ConfirmInventoryRequest;
import com.ecommerce.order.client.dto.InventoryAvailabilityResponse;
import com.ecommerce.order.client.dto.ReleaseInventoryRequest;
import com.ecommerce.order.client.dto.ReserveInventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Feign client for communicating with the Inventory Service.
 *
 * <p>All methods are protected by a Resilience4j circuit breaker named
 * {@code inventoryService}, configured in {@code order-service.yml}.
 * Fallback behaviour is provided by {@link InventoryServiceClientFallbackFactory}.</p>
 */
@FeignClient(
        name = "INVENTORY-SERVICE",
        fallbackFactory = InventoryServiceClientFallbackFactory.class
)
public interface InventoryServiceClient {

    @GetMapping("/api/v1/inventory/check/{productId}")
    ResponseEntity<InventoryAvailabilityResponse> checkAvailability(@PathVariable("productId") UUID productId);

    @PatchMapping("/api/v1/inventory/reserve")
    ResponseEntity<Object> reserveStock(@RequestBody ReserveInventoryRequest request);

    @PatchMapping("/api/v1/inventory/release")
    ResponseEntity<Object> releaseStock(@RequestBody ReleaseInventoryRequest request);

    @PatchMapping("/api/v1/inventory/confirm")
    ResponseEntity<Object> confirmStock(@RequestBody ConfirmInventoryRequest request);
}
