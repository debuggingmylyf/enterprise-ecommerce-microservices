package com.ecommerce.product.dto.response.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CategoryResponse {

    private UUID id;

    private UUID parentCategoryId;

    private String name;

    private String description;

    private Boolean active;
}
