package com.ecommerce.product.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductImageRequest {

    @NotBlank
    private String imageUrl;

    private Integer displayOrder;

    private Boolean primary;
}
