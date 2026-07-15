package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Order} entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Find an order by its unique order number.
     *
     * @param orderNumber the unique order number
     * @return the order if found, otherwise empty
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find all orders created by a specific user.
     *
     * @param userId the UUID of the user
     * @return list of orders
     */
    List<Order> findByUserId(UUID userId);
}
