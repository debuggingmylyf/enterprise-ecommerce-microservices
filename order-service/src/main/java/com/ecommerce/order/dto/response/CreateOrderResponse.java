package com.ecommerce.order.dto.response;

import com.ecommerce.order.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO returned after successfully creating an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponse {

    private UUID id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
}
