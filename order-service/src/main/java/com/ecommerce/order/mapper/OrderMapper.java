package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.response.CreateOrderResponse;
import com.ecommerce.order.dto.response.OrderItemResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.dto.response.OrderSummaryResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Spring-managed mapper responsible for mapping Order and OrderItem aggregates
 * to response DTOs. Manual mapping is preferred.
 */
@Component
public class OrderMapper {

    /**
     * Map Order entity to CreateOrderResponse DTO.
     *
     * @param order the source entity
     * @return CreateOrderResponse DTO
     */
    public CreateOrderResponse toCreateOrderResponse(final Order order) {
        if (order == null) {
            return null;
        }
        return CreateOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .build();
    }

    /**
     * Map OrderItem entity to OrderItemResponse DTO.
     *
     * @param item the source entity
     * @return OrderItemResponse DTO
     */
    public OrderItemResponse toOrderItemResponse(final OrderItem item) {
        if (item == null) {
            return null;
        }
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    /**
     * Map Order entity to OrderResponse DTO.
     *
     * @param order the source entity
     * @return OrderResponse DTO
     */
    public OrderResponse toOrderResponse(final Order order) {
        if (order == null) {
            return null;
        }
        final List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream().map(this::toOrderItemResponse).toList()
                : Collections.emptyList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddressLine1(order.getShippingAddressLine1())
                .shippingAddressLine2(order.getShippingAddressLine2())
                .city(order.getCity())
                .state(order.getState())
                .country(order.getCountry())
                .postalCode(order.getPostalCode())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
    }

    /**
     * Map Order entity to OrderSummaryResponse DTO.
     *
     * @param order the source entity
     * @return OrderSummaryResponse DTO
     */
    public OrderSummaryResponse toOrderSummaryResponse(final Order order) {
        if (order == null) {
            return null;
        }
        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
