package com.ecommerce.product.dto.response.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read-only response payload representing the pricing snapshot for a product.
 */
@Getter
@Setter
@Builder
public class ProductPricingResponse {

    private UUID id;

    private BigDecimal basePrice;

    private BigDecimal discountPrice;

    private String currency;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;
}

