package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.request.category.CreateCategoryRequest;
import com.ecommerce.product.dto.response.category.CategoryResponse;
import com.ecommerce.product.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Spring-managed mapper responsible for converting between {@link Category}
 * entities and their corresponding request/response DTOs.
 *
 * <p>Manual mapping is used intentionally (MapStruct is not on the classpath).
 */
@Component
public class CategoryMapper {

    /**
     * Maps a {@link Category} entity to its {@link CategoryResponse} DTO.
     *
     * @param category the source entity; must not be {@code null}
     * @return a populated {@link CategoryResponse}; never {@code null}
     * @throws IllegalArgumentException if {@code category} is {@code null}
     */
    public CategoryResponse toResponse(final Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .parentCategoryId(
                        category.getParentCategory() != null
                                ? category.getParentCategory().getId()
                                : null
                )
                .build();
    }

    /**
     * Maps a {@link CreateCategoryRequest} DTO to a new {@link Category} entity.
     *
     * <p>The {@code parentCategory} relationship is <em>not</em> populated here;
     * it is resolved and set by {@link com.ecommerce.product.serviceImpl.CategoryServiceImpl}.
     *
     * @param request the create request DTO; must not be {@code null}
     * @return a transient {@link Category} ready for persistence; never {@code null}
     * @throws IllegalArgumentException if {@code request} is {@code null}
     */
    public Category toEntity(final CreateCategoryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateCategoryRequest must not be null");
        }

        return Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .active(true)
                .build();
    }
}

