package com.ecommerce.product.specification;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductPricing;
import com.ecommerce.product.enums.ProductStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA Specification factory for composable, dynamic {@link Product} queries.
 *
 * <p>Each method returns a {@link Specification} predicate that can be combined
 * with {@code Specification.where(…).and(…)} chains in {@link com.ecommerce.product.serviceImpl.SearchServiceImpl}.
 *
 * <p>This class is a pure static factory and must not be instantiated.
 */
public final class ProductSpecification {

    private ProductSpecification() {
        throw new UnsupportedOperationException("ProductSpecification is a utility class");
    }

    /**
     * Matches products whose name or short description contains the given keyword
     * (case-insensitive LIKE search).
     *
     * @param keyword the search term; ignored if {@code null} or blank
     * @return a {@link Specification} for the keyword filter
     */
    public static Specification<Product> hasKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            final String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("shortDescription")), pattern),
                    cb.like(cb.lower(root.get("brand")), pattern)
            );
        };
    }

    /**
     * Matches products belonging to the given category.
     *
     * @param categoryId the UUID of the target category; ignored if {@code null}
     * @return a {@link Specification} for the category filter
     */
    public static Specification<Product> hasCategory(final UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    /**
     * Matches products with the given brand name (case-insensitive exact match).
     *
     * @param brand the brand name to match; ignored if {@code null} or blank
     * @return a {@link Specification} for the brand filter
     */
    public static Specification<Product> hasBrand(final String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
        };
    }

    /**
     * Matches products whose base price falls within the given inclusive range.
     * Either bound may be {@code null} to represent an open-ended range.
     *
     * @param minPrice minimum base price (inclusive); ignored if {@code null}
     * @param maxPrice maximum base price (inclusive); ignored if {@code null}
     * @return a {@link Specification} for the price range filter
     */
    public static Specification<Product> hasPriceRange(
            final BigDecimal minPrice,
            final BigDecimal maxPrice) {

        return (root, query, cb) -> {
            final Join<Product, ProductPricing> pricingJoin =
                    root.join("pricing", JoinType.LEFT);

            if (minPrice != null && maxPrice != null) {
                return cb.between(pricingJoin.get("basePrice"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(pricingJoin.get("basePrice"), minPrice);
            } else if (maxPrice != null) {
                return cb.lessThanOrEqualTo(pricingJoin.get("basePrice"), maxPrice);
            }
            return cb.conjunction();
        };
    }

    /**
     * Matches products with the given {@link ProductStatus}.
     *
     * @param status the status to filter by; ignored if {@code null}
     * @return a {@link Specification} for the status filter
     */
    public static Specification<Product> hasStatus(final ProductStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }
}

