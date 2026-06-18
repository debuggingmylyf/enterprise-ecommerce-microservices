package com.ecommerce.product.dto.response.product;

import com.ecommerce.product.enums.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Lightweight response returned upon successful product creation,
 * containing only the identifiers and initial status — consumers
 * use {@code GET /api/v1/products/{id}} for the full representation.
 */
@Getter
@Builder
public class CreateProductResponse {

    private UUID productId;

    private String skuCode;

    private ProductStatus status;

    private String message;
}