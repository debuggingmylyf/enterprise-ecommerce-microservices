package com.ecommerce.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA auditing for automatic population of
 * {@code createdAt} and {@code updatedAt} fields on all entities
 * extending {@link com.ecommerce.inventory.entity.BaseEntity}.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
