package com.ecommerce.order.client;

import com.ecommerce.order.client.dto.ProductClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Fallback factory for {@link ProductServiceClient}.
 * Returns safe degraded responses when product service is unavailable or circuit breaks.
 */
@Component
public class ProductServiceClientFallbackFactory implements FallbackFactory<ProductServiceClient> {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceClientFallbackFactory.class);

    @Override
    public ProductServiceClient create(final Throwable cause) {
        log.warn("ProductServiceClient fallback triggered. Cause: {}", cause.getMessage());

        return new ProductServiceClient() {
            @Override
            public ResponseEntity<ProductClientResponse> getProductById(final UUID id) {
                log.warn("getProductById fallback triggered for product ID: {}", id);
                return ResponseEntity.ok(null);
            }
        };
    }
}
