package com.ecommerce.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Request DTO representing a request to create a new order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressRequest shippingAddress;
}
