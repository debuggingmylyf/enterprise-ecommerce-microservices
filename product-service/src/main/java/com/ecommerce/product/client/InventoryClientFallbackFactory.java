package com.ecommerce.product.client;

import com.ecommerce.product.dto.request.inventory.CreateInventoryRequest;
import com.ecommerce.product.dto.response.inventory.CreateInventoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Feign fallback factory for {@link InventoryClient}.
 *
 * <p>When the Resilience4j circuit breaker trips (or inventory-service is
 * unreachable), this factory creates an {@link InventoryClient} implementation
 * that returns safe degraded responses instead of propagating the exception
 * up the call stack.</p>
 *
 * <p>Callers inspect the returned {@code ResponseEntity} status or payload
 * to decide how to surface the degraded state (e.g. include a warning in
 * the product creation response).</p>
 */
@Component
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    private static final Logger log = LoggerFactory.getLogger(InventoryClientFallbackFactory.class);

    @Override
    public InventoryClient create(final Throwable cause) {
        log.warn("InventoryClient fallback triggered – inventory-service may be unavailable. Cause: {}",
                cause.getMessage());

        return new InventoryClient() {

            /**
             * Returns a sentinel {@link CreateInventoryResponse} signalling that
             * provisioning was deferred. The product creation still succeeds; the
             * caller is responsible for including a warning in the API response.
             */
            @Override
            public ResponseEntity<CreateInventoryResponse> provisionInventory(
                    final CreateInventoryRequest request) {

                log.warn("provisionInventory fallback – productId={}, warehouse={}. "
                        + "Inventory must be provisioned manually.",
                        request.getProductId(), request.getWarehouseCode());

                // Return a sentinel response; the null inventoryId signals "not yet provisioned"
                CreateInventoryResponse fallbackResponse = CreateInventoryResponse.builder()
                        .inventoryId(null)
                        .productId(request.getProductId())
                        .warehouseCode(request.getWarehouseCode())
                        .message("DEFERRED – inventory-service unavailable; "
                                + "manual provisioning required for product "
                                + request.getProductId())
                        .build();

                return ResponseEntity.ok(fallbackResponse);
            }

            @Override
            public ResponseEntity<Object> checkAvailability(final UUID productId) {
                log.warn("checkAvailability fallback – productId={}. Returning unavailable.", productId);
                return ResponseEntity.ok(null);
            }
        };
    }
}
