package com.ecommerce.product.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank
    private String name;

    private String shortDescription;

    private String fullDescription;

    private String brand;

    private UUID categoryId;

    private BigDecimal basePrice;

    private BigDecimal discountPrice;
}
