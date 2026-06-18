package com.ecommerce.product.dto.response.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Read-only response payload representing a single product image.
 */
@Getter
@Setter
@Builder
public class ProductImageResponse {

    private UUID id;

    private String imageUrl;

    private Integer displayOrder;

    private Boolean isPrimary;
}

