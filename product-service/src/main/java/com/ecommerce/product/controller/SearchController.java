package com.ecommerce.product.controller;

import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.product.dto.request.product.ProductSearchRequest;
import com.ecommerce.product.dto.response.product.ProductResponse;
import com.ecommerce.product.enums.ProductStatus;
import com.ecommerce.product.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST controller exposing product search capabilities.
 *
 * <p>Base path: {@code /api/v1/search}
 *
 * <p>All filter parameters are optional; omitted parameters are treated as wildcards.
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final SearchService searchService;

    /**
     * Searches for products matching the supplied optional filter criteria.
     *
     * @param keyword   free-text search across name, short description, and brand
     * @param categoryId filter by category UUID
     * @param brand     filter by brand name (case-insensitive exact match)
     * @param minPrice  minimum base price (inclusive)
     * @param maxPrice  maximum base price (inclusive)
     * @param status    filter by {@link ProductStatus}
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 20)
     * @param sortBy    field to sort by (default: {@code createdAt})
     * @param direction sort direction, {@code ASC} or {@code DESC} (default: {@code DESC})
     * @return {@code 200 OK} with a {@link PaginatedResponse} of {@link ProductResponse}
     */
    @GetMapping("/products")
    public ResponseEntity<PaginatedResponse<ProductResponse>> searchProducts(
            @RequestParam(required = false) final String keyword,
            @RequestParam(required = false) final UUID categoryId,
            @RequestParam(required = false) final String brand,
            @RequestParam(required = false) final BigDecimal minPrice,
            @RequestParam(required = false) final BigDecimal maxPrice,
            @RequestParam(required = false) final ProductStatus status,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(defaultValue = "createdAt") final String sortBy,
            @RequestParam(defaultValue = "DESC") final String direction) {

        log.debug("GET /api/v1/search/products – keyword='{}', page={}, size={}", keyword, page, size);

        final ProductSearchRequest searchRequest = ProductSearchRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .brand(brand)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .status(status)
                .build();

        final Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(searchService.searchProducts(searchRequest, pageable));
    }
}

