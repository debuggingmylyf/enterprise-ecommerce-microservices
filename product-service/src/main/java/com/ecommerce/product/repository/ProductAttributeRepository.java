package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link ProductAttribute} entities.
 */
@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, UUID> {

    /**
     * Returns all attributes belonging to the given product.
     *
     * @param productId the UUID of the owning product
     * @return list of attributes; never {@code null}
     */
    List<ProductAttribute> findAllByProductId(UUID productId);

    /**
     * Deletes all attributes associated with the given product.
     * Used during product update to replace the attribute set.
     *
     * @param productId the UUID of the owning product
     */
    void deleteAllByProductId(UUID productId);
}

