package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Category} entities.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD and pagination
 * operations, and declares custom query methods for domain-specific lookups.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Returns whether a category with the given name already exists (case-sensitive).
     *
     * @param name the category name to check
     * @return {@code true} if a matching record exists
     */
    boolean existsByName(String name);

    /**
     * Returns whether a category with the given name exists, excluding a specific ID.
     * Used during update operations to allow keeping the same name.
     *
     * @param name the category name to check
     * @param id   the UUID of the category being updated (to exclude from the check)
     * @return {@code true} if another category with that name exists
     */
    boolean existsByNameAndIdNot(String name, UUID id);

    /**
     * Finds a category by its exact name.
     *
     * @param name the category name
     * @return an {@link Optional} containing the category, or empty if none found
     */
    Optional<Category> findByName(String name);

    /**
     * Returns all active (non-deactivated) categories.
     *
     * @return list of active {@link Category} entities; never {@code null}
     */
    List<Category> findAllByActiveTrue();
}

