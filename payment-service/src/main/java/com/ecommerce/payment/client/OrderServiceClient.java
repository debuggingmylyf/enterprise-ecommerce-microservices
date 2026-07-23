package com.ecommerce.payment.client;

import com.ecommerce.payment.client.dto.OrderClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Feign client for communicating with the Order Service.
 *
 * <p>All methods are protected by a Resilience4j circuit breaker named
 * {@code orderService}. Fallback behaviour is provided by
 * {@link OrderServiceClientFallbackFactory}.</p>
 */
@FeignClient(
        name = "ORDER-SERVICE",
        fallbackFactory = OrderServiceClientFallbackFactory.class
)
public interface OrderServiceClient {

    @GetMapping("/api/v1/internal/orders/{id}")
    ResponseEntity<OrderClientResponse> getOrderById(@PathVariable("id") UUID orderId);

    @PatchMapping("/api/v1/internal/orders/{id}/payment-success")
    ResponseEntity<Object> markPaymentSuccess(@PathVariable("id") UUID orderId);

    @PatchMapping("/api/v1/internal/orders/{id}/payment-failed")
    ResponseEntity<Object> markPaymentFailed(@PathVariable("id") UUID orderId);
}
