package com.ecommerce.product.dto.response.product;

import com.ecommerce.product.enums.ProductStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public class CreateProductResponse {

    private UUID productId;

    private String skuCode;

    private ProductStatus status;

    private String message;
}