package com.ecommerce.product.service;

import com.ecommerce.product.dto.request.category.CreateCategoryRequest;
import com.ecommerce.product.dto.request.category.UpdateCategoryReqest;
import com.ecommerce.product.dto.response.category.CategoryResponse;
import com.ecommerce.product.exception.BusinessException;
import com.ecommerce.product.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for category management operations.
 *
 * <p>Implementations are responsible for enforcing business rules (e.g. uniqueness
 * constraints) and delegating persistence to the repository layer.
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param request the creation payload; must not be {@code null}
     * @return the persisted category as a {@link CategoryResponse}
     * @throws BusinessException if a category with the same name already exists
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Updates an existing category identified by its UUID.
     *
     * @param id      the UUID of the category to update; must not be {@code null}
     * @param request the update payload; must not be {@code null}
     * @return the updated category as a {@link CategoryResponse}
     * @throws ResourceNotFoundException if no category with the given ID exists
     * @throws BusinessException         if the new name conflicts with another category
     */
    CategoryResponse updateCategory(UUID id, UpdateCategoryReqest request);

    /**
     * Retrieves a single category by its UUID.
     *
     * @param id the UUID of the desired category; must not be {@code null}
     * @return the matching {@link CategoryResponse}
     * @throws ResourceNotFoundException if no category with the given ID exists
     */
    CategoryResponse getCategoryById(UUID id);

    /**
     * Returns all active categories in the system.
     *
     * @return an unmodifiable list of {@link CategoryResponse}; never {@code null}
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Soft-deletes a category by marking it inactive.
     *
     * @param id the UUID of the category to deactivate; must not be {@code null}
     * @throws ResourceNotFoundException if no category with the given ID exists
     */
    void deleteCategory(UUID id);
}

