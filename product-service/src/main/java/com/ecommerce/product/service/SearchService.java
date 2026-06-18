package com.ecommerce.product.service;

import com.ecommerce.product.dto.common.PaginatedResponse;
import com.ecommerce.product.dto.request.product.ProductSearchRequest;
import com.ecommerce.product.dto.response.product.ProductResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for product search operations.
 *
 * <p>Accepts a composite filter request and returns a paginated result set.
 * Implementations use JPA Specifications for dynamic predicate composition.
 */
public interface SearchService {

    /**
     * Searches for products matching the given filter criteria.
     *
     * <p>All filter fields in {@link ProductSearchRequest} are optional;
     * omitted fields are treated as wildcards.
     *
     * @param searchRequest the filter criteria; must not be {@code null}
     * @param pageable      pagination and sorting parameters; must not be {@code null}
     * @return a {@link PaginatedResponse} of matching {@link ProductResponse} objects;
     *         never {@code null}, may be empty
     */
    PaginatedResponse<ProductResponse> searchProducts(
            ProductSearchRequest searchRequest,
            Pageable pageable);
}

