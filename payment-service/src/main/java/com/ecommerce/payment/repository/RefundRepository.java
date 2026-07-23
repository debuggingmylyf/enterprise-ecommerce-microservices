package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for {@link Refund} entities.
 */
public interface RefundRepository extends JpaRepository<Refund, UUID> {

    List<Refund> findAllByPaymentId(UUID paymentId);
}
