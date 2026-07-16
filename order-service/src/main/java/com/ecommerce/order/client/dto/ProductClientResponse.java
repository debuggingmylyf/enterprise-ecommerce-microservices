package com.ecommerce.order.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Lightweight representation of product information received from Product Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductClientResponse {
    private UUID id;
    private String name;
    private ProductPricingClientResponse pricing;
}
