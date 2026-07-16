package com.ecommerce.order.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Lightweight representation of product pricing information received from Product Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPricingClientResponse {
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
}
