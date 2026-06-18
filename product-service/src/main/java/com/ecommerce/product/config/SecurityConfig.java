package com.ecommerce.product.config;

import org.springframework.context.annotation.Configuration;

/**
 * Security configuration placeholder for the Product Service.
 *
 * <p><strong>Architecture note:</strong> The Product Service does not include
 * {@code spring-boot-starter-security} as a dependency. Authentication and
 * authorisation are enforced at the API Gateway layer (see {@code api-gateway} module).
 * This service therefore trusts all inbound traffic that has been routed through
 * the gateway and does not perform its own token validation.
 *
 * <p>If field-level security or method-level access control is needed in future,
 * add {@code spring-boot-starter-security} to the POM and implement the
 * {@code SecurityFilterChain} bean here.
 */
@Configuration
public class SecurityConfig {
    // No Spring Security configuration — gateway-enforced authentication model.
}


