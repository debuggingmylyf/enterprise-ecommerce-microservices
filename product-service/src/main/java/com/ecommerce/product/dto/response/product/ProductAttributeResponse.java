package com.ecommerce.product.dto.response.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Read-only response payload representing a single product attribute (key-value pair).
 */
@Getter
@Setter
@Builder
public class ProductAttributeResponse {

    private UUID id;

    private String attributeName;

    private String attributeValue;
}

