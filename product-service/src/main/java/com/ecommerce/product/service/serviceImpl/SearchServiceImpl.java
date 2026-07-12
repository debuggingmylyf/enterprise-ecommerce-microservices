package com.ecommerce.product.service.serviceImpl;

import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.product.dto.request.product.ProductSearchRequest;
import com.ecommerce.product.dto.response.product.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.SearchService;
import com.ecommerce.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link SearchService}.
 *
 * <p>Builds a composed JPA {@link Specification} from the provided search request
 * using static factory methods in {@link ProductSpecification}, then delegates
 * to the {@link ProductRepository} which implements {@code JpaSpecificationExecutor}.
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * {@inheritDoc}
     *
     * <p>Each filter predicate is composed with a logical AND. Null or blank filter
     * values are silently ignored (treated as "match all").
     *
     * @param searchRequest the filter criteria; must not be {@code null}
     * @param pageable      pagination and sorting parameters; must not be {@code null}
     * @return a {@link PaginatedResponse} of matching {@link ProductResponse}; never {@code null}
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProductResponse> searchProducts(
            final ProductSearchRequest searchRequest,
            final Pageable pageable) {

        if (searchRequest == null) {
            throw new IllegalArgumentException("SearchRequest must not be null");
        }

        final Specification<Product> spec = Specification
                .where(ProductSpecification.hasKeyword(searchRequest.getKeyword()))
                .and(ProductSpecification.hasCategory(searchRequest.getCategoryId()))
                .and(ProductSpecification.hasBrand(searchRequest.getBrand()))
                .and(ProductSpecification.hasPriceRange(
                        searchRequest.getMinPrice(),
                        searchRequest.getMaxPrice()))
                .and(ProductSpecification.hasStatus(searchRequest.getStatus()));

        final Page<Product> page = productRepository.findAll(spec, pageable);

        final List<ProductResponse> content = page.getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();

        log.debug("Search returned {} results (page={}, size={})",
                page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());

        return PaginatedResponse.<ProductResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}

