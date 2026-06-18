package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link ProductPricing} entities.
 */
@Repository
public interface ProductPricingRepository extends JpaRepository<ProductPricing, UUID> {

    /**
     * Finds the pricing record associated with the given product.
     *
     * @param product the owning {@link Product}
     * @return an {@link Optional} containing the pricing, or empty if none defined
     */
    Optional<ProductPricing> findByProduct(Product product);

    /**
     * Finds the pricing record associated with a product identified by its UUID.
     *
     * @param productId the UUID of the owning product
     * @return an {@link Optional} containing the pricing, or empty if none defined
     */
    Optional<ProductPricing> findByProductId(UUID productId);
}

