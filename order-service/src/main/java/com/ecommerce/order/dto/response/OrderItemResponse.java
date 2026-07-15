package com.ecommerce.order.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO representing an item in an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private UUID id;
    private UUID productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
