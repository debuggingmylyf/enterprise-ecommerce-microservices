package com.ecommerce.product.dto.request.product;

import com.ecommerce.product.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductStatusRequest {

    @NotNull
    private ProductStatus status;
}
