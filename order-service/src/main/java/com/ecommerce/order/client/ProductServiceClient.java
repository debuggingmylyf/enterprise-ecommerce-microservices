package com.ecommerce.order.client;

import com.ecommerce.order.client.dto.ProductClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Feign client for communicating with the Product Service.
 *
 * <p>All methods are protected by a Resilience4j circuit breaker named
 * {@code productService}, configured in {@code order-service.yml}.
 * Fallback behaviour is provided by {@link ProductServiceClientFallbackFactory}.</p>
 */
@FeignClient(
        name = "PRODUCT-SERVICE",
        fallbackFactory = ProductServiceClientFallbackFactory.class
)
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{id}")
    ResponseEntity<ProductClientResponse> getProductById(@PathVariable("id") UUID id);
}
