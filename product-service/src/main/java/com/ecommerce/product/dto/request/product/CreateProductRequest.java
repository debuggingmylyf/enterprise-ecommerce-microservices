package com.ecommerce.product.dto.request.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Short description is required")
    @Size(max = 500)
    private String shortDescription;

    private String fullDescription;

    @NotBlank
    private String brand;

    @NotNull
    private UUID categoryId;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.0")
    private BigDecimal discountPrice;

    @NotEmpty
    private List<ProductImageRequest> images;

    private Set<ProductAttributeRequest> attributes;
}
