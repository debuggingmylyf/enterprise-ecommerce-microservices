package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p>Also extends {@link JpaSpecificationExecutor} to support dynamic,
 * criteria-based queries used by the search feature.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {

    /**
     * Returns whether a product with the given SKU code already exists.
     *
     * @param skuCode the SKU to check
     * @return {@code true} if a matching record exists
     */
    boolean existsBySkuCode(String skuCode);

    /**
     * Returns whether a product with the given URL slug already exists.
     *
     * @param slug the slug to check
     * @return {@code true} if a matching record exists
     */
    boolean existsBySlug(String slug);

    /**
     * Finds a product by its URL slug.
     *
     * @param slug the unique slug value
     * @return an {@link Optional} containing the product, or empty if none found
     */
    Optional<Product> findBySlug(String slug);

    /**
     * Returns all products that are currently active.
     *
     * @return list of active {@link Product} entities; never {@code null}
     */
    List<Product> findAllByActiveTrue();
}

