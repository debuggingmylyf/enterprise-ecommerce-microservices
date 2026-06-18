package com.ecommerce.product.service;

import com.ecommerce.product.dto.common.PaginatedResponse;
import com.ecommerce.product.dto.request.product.CreateProductRequest;
import com.ecommerce.product.dto.request.product.UpdateProductRequest;
import com.ecommerce.product.dto.request.product.UpdateProductStatusRequest;
import com.ecommerce.product.dto.response.product.CreateProductResponse;
import com.ecommerce.product.dto.response.product.ProductResponse;
import com.ecommerce.product.exception.BusinessException;
import com.ecommerce.product.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service contract for product lifecycle management operations.
 *
 * <p>All write operations are transactional. Implementations must enforce
 * domain invariants such as SKU uniqueness and slug uniqueness.
 */
public interface ProductService {

    /**
     * Creates a new product with its associated pricing, images, and attributes.
     *
     * @param request the creation payload; must not be {@code null}
     * @return a lightweight {@link CreateProductResponse} with the new product's identifiers
     * @throws ResourceNotFoundException if the referenced category does not exist
     * @throws BusinessException         if the SKU code is already taken
     */
    CreateProductResponse createProduct(CreateProductRequest request);

    /**
     * Partially updates a product's core fields.
     *
     * @param id      the UUID of the product to update; must not be {@code null}
     * @param request the update payload; must not be {@code null}
     * @return the updated product as a full {@link ProductResponse}
     * @throws ResourceNotFoundException if no product with the given ID exists
     * @throws ResourceNotFoundException if the referenced category does not exist
     */
    ProductResponse updateProduct(UUID id, UpdateProductRequest request);

    /**
     * Retrieves a product by its primary key.
     *
     * @param id the UUID of the desired product; must not be {@code null}
     * @return the matching {@link ProductResponse}
     * @throws ResourceNotFoundException if no product with the given ID exists
     */
    ProductResponse getProductById(UUID id);

    /**
     * Retrieves a product by its URL slug.
     *
     * @param slug the unique slug value; must not be {@code null} or blank
     * @return the matching {@link ProductResponse}
     * @throws ResourceNotFoundException if no product with the given slug exists
     */
    ProductResponse getProductBySlug(String slug);

    /**
     * Returns a paginated list of all active products.
     *
     * @param pageable pagination and sorting parameters; must not be {@code null}
     * @return a {@link PaginatedResponse} of {@link ProductResponse} objects
     */
    PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable);

    /**
     * Updates the lifecycle status of a product (e.g. ACTIVE → DISCONTINUED).
     *
     * @param id      the UUID of the product; must not be {@code null}
     * @param request the new status; must not be {@code null}
     * @return the updated product as a {@link ProductResponse}
     * @throws ResourceNotFoundException if no product with the given ID exists
     */
    ProductResponse updateProductStatus(UUID id, UpdateProductStatusRequest request);

    /**
     * Soft-deletes a product by marking it inactive.
     *
     * @param id the UUID of the product to deactivate; must not be {@code null}
     * @throws ResourceNotFoundException if no product with the given ID exists
     */
    void deleteProduct(UUID id);
}

