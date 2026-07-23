package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller exposing internal endpoints for inter-service communication.
 *
 * <p>Base path: {@code /api/v1/internal/payments}</p>
 */
@RestController
@RequestMapping("/api/v1/internal/payments")
@RequiredArgsConstructor
@Slf4j
public class InternalPaymentController {

    private final PaymentService paymentService;

    /**
     * Internal endpoint to fetch payment status by order ID.
     * Used by other services for order-payment correlation.
     *
     * @param orderId the UUID of the order
     * @return the payment response
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable final UUID orderId) {
        log.info("Internal GET payment for order: {}", orderId);
        final PaymentResponse response = paymentService.getPaymentByOrderIdInternal(orderId);
        return ResponseEntity.ok(response);
    }
}
