package com.ecommerce.order.client;

import com.ecommerce.order.client.dto.ConfirmInventoryRequest;
import com.ecommerce.order.client.dto.InventoryAvailabilityResponse;
import com.ecommerce.order.client.dto.ReleaseInventoryRequest;
import com.ecommerce.order.client.dto.ReserveInventoryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Fallback factory for {@link InventoryServiceClient}.
 * Returns safe degraded responses when inventory service is unavailable or circuit breaks.
 */
@Component
public class InventoryServiceClientFallbackFactory implements FallbackFactory<InventoryServiceClient> {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceClientFallbackFactory.class);

    @Override
    public InventoryServiceClient create(final Throwable cause) {
        log.warn("InventoryServiceClient fallback triggered. Cause: {}", cause.getMessage());

        return new InventoryServiceClient() {
            @Override
            public ResponseEntity<InventoryAvailabilityResponse> checkAvailability(final UUID productId) {
                log.warn("checkAvailability fallback triggered for product ID: {}", productId);
                return ResponseEntity.ok(null);
            }

            @Override
            public ResponseEntity<Object> reserveStock(final ReserveInventoryRequest request) {
                log.warn("reserveStock fallback triggered for product ID: {}", request.getProductId());
                return ResponseEntity.ok(null);
            }

            @Override
            public ResponseEntity<Object> releaseStock(final ReleaseInventoryRequest request) {
                log.warn("releaseStock fallback triggered for product ID: {}", request.getProductId());
                return ResponseEntity.ok(null);
            }

            @Override
            public ResponseEntity<Object> confirmStock(final ConfirmInventoryRequest request) {
                log.warn("confirmStock fallback triggered for product ID: {}", request.getProductId());
                return ResponseEntity.ok(null);
            }
        };
    }
}
