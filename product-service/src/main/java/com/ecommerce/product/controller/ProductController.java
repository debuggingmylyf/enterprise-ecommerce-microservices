package com.ecommerce.product.controller;

import com.ecommerce.product.dto.common.PaginatedResponse;
import com.ecommerce.product.dto.request.product.CreateProductRequest;
import com.ecommerce.product.dto.request.product.UpdateProductRequest;
import com.ecommerce.product.dto.request.product.UpdateProductStatusRequest;
import com.ecommerce.product.dto.response.product.CreateProductResponse;
import com.ecommerce.product.dto.response.product.ProductResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller exposing CRUD and lifecycle management endpoints for products.
 *
 * <p>Base path: {@code /api/v1/products}
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    private static String sanitize(final String value) {
        return value == null ? null : value.replaceAll("[\n\r]", "_");
    }

    /**
     * Creates a new product in DRAFT status.
     *
     * @param request the validated creation payload
     * @return {@code 201 Created} with the lightweight {@link CreateProductResponse}
     */
    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(
            @Valid @RequestBody final CreateProductRequest request) {

        log.info("POST /api/v1/products – creating product '{}'", sanitize(request.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    /**
     * Partially updates a product's core fields.
     *
     * @param id      the UUID of the product to update
     * @param request the validated update payload
     * @return {@code 200 OK} with the full updated {@link ProductResponse}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable final UUID id,
            @Valid @RequestBody final UpdateProductRequest request) {

        log.info("PUT /api/v1/products/{}", id);
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * Retrieves a product by its UUID.
     *
     * @param id the UUID of the desired product
     * @return {@code 200 OK} with the full {@link ProductResponse}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable final UUID id) {
        log.debug("GET /api/v1/products/{}", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Retrieves a product by its URL slug.
     *
     * @param slug the unique slug value
     * @return {@code 200 OK} with the full {@link ProductResponse}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable final String slug) {
        log.debug("GET /api/v1/products/slug/{}", sanitize(slug));
        return ResponseEntity.ok(productService.getProductBySlug(slug));
    }

    /**
     * Returns a paginated list of all products.
     *
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 20)
     * @param sortBy    field to sort by (default: {@code createdAt})
     * @param direction sort direction, {@code ASC} or {@code DESC} (default: {@code DESC})
     * @return {@code 200 OK} with a {@link PaginatedResponse} of {@link ProductResponse}
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(defaultValue = "createdAt") final String sortBy,
            @RequestParam(defaultValue = "DESC") final String direction) {

        log.debug("GET /api/v1/products – page={}, size={}", page, size);
        final Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    /**
     * Updates the lifecycle status of a product (e.g. DRAFT → ACTIVE).
     *
     * @param id      the UUID of the product
     * @param request the new status value
     * @return {@code 200 OK} with the updated {@link ProductResponse}
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable final UUID id,
            @Valid @RequestBody final UpdateProductStatusRequest request) {

        log.info("PATCH /api/v1/products/{}/status – new status={}", id, sanitize(String.valueOf(request.getStatus())));
        return ResponseEntity.ok(productService.updateProductStatus(id, request));
    }

    /**
     * Soft-deletes a product (marks it inactive).
     *
     * @param id the UUID of the product to deactivate
     * @return {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable final UUID id) {
        log.info("DELETE /api/v1/products/{}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

