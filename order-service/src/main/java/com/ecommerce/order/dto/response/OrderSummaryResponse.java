package com.ecommerce.order.dto.response;

import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing summary level details of an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {

    private UUID id;
    private String orderNumber;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
