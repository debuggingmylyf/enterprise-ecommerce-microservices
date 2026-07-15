package com.ecommerce.order.dto.response;

import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO representing detailed information of an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID id;
    private String orderNumber;
    private UUID userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
}
