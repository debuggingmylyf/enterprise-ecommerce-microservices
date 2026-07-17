package com.ecommerce.order.controller;

import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller exposing internal endpoints for inter-service communication
 * (e.g. callbacks from Payment Service).
 *
 * <p>Base path: {@code /api/v1/internal/orders}</p>
 */
@RestController
@RequestMapping("/api/v1/internal/orders")
@RequiredArgsConstructor
@Slf4j
public class InternalOrderController {

    private final OrderService orderService;

    /**
     * Internal callback to notify that payment was successful.
     * Transition order status to CONFIRMED.
     *
     * @param id the UUID of the order
     * @return the updated order response
     */
    @PatchMapping("/{id}/payment-success")
    public ResponseEntity<OrderResponse> markPaymentSuccess(@PathVariable final UUID id) {
        log.info("Received internal success callback for order: {}", id);
        final OrderResponse response = orderService.markPaymentSuccess(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Internal callback to notify that payment failed.
     * Transition order status to CANCELLED and release inventory stock.
     *
     * @param id the UUID of the order
     * @return the updated order response
     */
    @PatchMapping("/{id}/payment-failed")
    public ResponseEntity<OrderResponse> markPaymentFailed(@PathVariable final UUID id) {
        log.info("Received internal failure callback for order: {}", id);
        final OrderResponse response = orderService.markPaymentFailed(id);
        return ResponseEntity.ok(response);
    }
}
