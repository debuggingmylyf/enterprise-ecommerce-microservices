package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link ProductImage} entities.
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    /**
     * Returns all images belonging to the given product.
     *
     * @param productId the UUID of the owning product
     * @return list of images; never {@code null}
     */
    List<ProductImage> findAllByProductId(UUID productId);

    /**
     * Deletes all images associated with the given product.
     * Used during product update to replace the image set.
     *
     * @param productId the UUID of the owning product
     */
    void deleteAllByProductId(UUID productId);
}

