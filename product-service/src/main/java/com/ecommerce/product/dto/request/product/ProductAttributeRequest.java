package com.ecommerce.product.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductAttributeRequest {

    @NotBlank
    private String attributeName;

    @NotBlank
    private String attributeValue;
}
