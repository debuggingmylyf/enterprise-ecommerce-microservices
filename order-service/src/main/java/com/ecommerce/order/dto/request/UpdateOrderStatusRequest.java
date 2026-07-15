package com.ecommerce.order.dto.request;

import com.ecommerce.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO representing a request to update the status of an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
