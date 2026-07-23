package com.ecommerce.payment.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lightweight DTO for order data fetched from Order Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderClientResponse {

    private UUID id;
    private String orderNumber;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
}
