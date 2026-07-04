package com.ecommerce.inventory.config;

import org.springframework.context.annotation.Configuration;

/**
 * Security configuration placeholder for the Inventory Service.
 *
 * <p><strong>Architecture note:</strong> The Inventory Service does not include
 * {@code spring-boot-starter-security} as a dependency. Authentication and
 * authorisation are enforced at the API Gateway layer. This service trusts all
 * inbound traffic routed through the gateway and does not perform its own token
 * validation, consistent with the product-service security model.
 *
 * <p>Role-level access control (ADMIN vs INTERNAL_SERVICE) is enforced at the
 * gateway routing layer using the {@code X-User-Role} header extracted from the JWT.
 *
 * <p>If method-level security is needed in future, add
 * {@code spring-boot-starter-security} to the POM and implement a
 * {@code SecurityFilterChain} bean here.
 */
@Configuration
public class SecurityConfig {
    // No Spring Security configuration — gateway-enforced authentication model.
}
