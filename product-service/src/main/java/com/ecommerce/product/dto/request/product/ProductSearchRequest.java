package com.ecommerce.product.dto.request.product;

import com.ecommerce.product.enums.ProductStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductSearchRequest {

    private String keyword;

    private UUID categoryId;

    private String brand;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private ProductStatus status;
}
