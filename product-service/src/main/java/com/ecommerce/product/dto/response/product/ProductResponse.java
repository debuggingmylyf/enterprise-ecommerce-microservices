package com.ecommerce.product.dto.response.product;

import com.ecommerce.product.dto.response.category.CategoryResponse;
import com.ecommerce.product.enums.ProductStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Builder
public class ProductResponse {

    private UUID id;

    private String name;

    private String slug;

    private String shortDescription;

    private String fullDescription;

    private String brand;

    private String skuCode;

    private ProductStatus status;

    private CategoryResponse category;

    private ProductPricingResponse pricing;

    private List<ProductImageResponse> images;

    private Set<ProductAttributeResponse> attributes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}