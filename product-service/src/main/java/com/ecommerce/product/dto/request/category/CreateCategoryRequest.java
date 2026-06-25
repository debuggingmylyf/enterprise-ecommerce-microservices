package com.ecommerce.product.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class CreateCategoryRequest {

    @NotBlank
    private String name;

    private String description;

    private UUID parentCategoryId;
}