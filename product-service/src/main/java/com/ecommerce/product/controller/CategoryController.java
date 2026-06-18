package com.ecommerce.product.controller;

import com.ecommerce.product.dto.request.category.CreateCategoryRequest;
import com.ecommerce.product.dto.request.category.UpdateCategoryReqest;
import com.ecommerce.product.dto.response.category.CategoryResponse;
import com.ecommerce.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing CRUD operations for product categories.
 *
 * <p>Base path: {@code /api/v1/categories}
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    /**
     * Creates a new product category.
     *
     * @param request the validated creation payload
     * @return {@code 201 Created} with the persisted {@link CategoryResponse}
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody final CreateCategoryRequest request) {

        log.info("POST /api/v1/categories – creating category '{}'", request.getName());
        final CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing category.
     *
     * @param id      the UUID of the category to update
     * @param request the validated update payload
     * @return {@code 200 OK} with the updated {@link CategoryResponse}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable final UUID id,
            @Valid @RequestBody final UpdateCategoryReqest request) {

        log.info("PUT /api/v1/categories/{}", id);
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    /**
     * Retrieves a category by its UUID.
     *
     * @param id the UUID of the desired category
     * @return {@code 200 OK} with the matching {@link CategoryResponse}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable final UUID id) {
        log.debug("GET /api/v1/categories/{}", id);
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * Returns all active categories.
     *
     * @return {@code 200 OK} with a list of {@link CategoryResponse}
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.debug("GET /api/v1/categories");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * Soft-deletes a category (marks it inactive).
     *
     * @param id the UUID of the category to deactivate
     * @return {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable final UUID id) {
        log.info("DELETE /api/v1/categories/{}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

