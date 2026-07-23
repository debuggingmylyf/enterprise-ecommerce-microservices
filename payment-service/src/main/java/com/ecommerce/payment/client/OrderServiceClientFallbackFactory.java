package com.ecommerce.payment.client;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.payment.client.dto.OrderClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Fallback factory for {@link OrderServiceClient}.
 *
 * <p>Logs the cause of the circuit-breaker trip and throws a
 * {@link BusinessException} so the caller can handle it gracefully.</p>
 */
@Component
@Slf4j
public class OrderServiceClientFallbackFactory implements FallbackFactory<OrderServiceClient> {

    @Override
    public OrderServiceClient create(final Throwable cause) {
        log.error("OrderServiceClient circuit breaker triggered: {}", cause.getMessage(), cause);

        return new OrderServiceClient() {
            @Override
            public ResponseEntity<OrderClientResponse> getOrderById(final UUID orderId) {
                throw new BusinessException("ORDER_SERVICE_UNAVAILABLE",
                        "Order service is unavailable. Cannot fetch order: " + orderId);
            }

            @Override
            public ResponseEntity<Object> markPaymentSuccess(final UUID orderId) {
                throw new BusinessException("ORDER_SERVICE_UNAVAILABLE",
                        "Order service is unavailable. Cannot notify payment success for order: " + orderId);
            }

            @Override
            public ResponseEntity<Object> markPaymentFailed(final UUID orderId) {
                throw new BusinessException("ORDER_SERVICE_UNAVAILABLE",
                        "Order service is unavailable. Cannot notify payment failure for order: " + orderId);
            }
        };
    }
}
