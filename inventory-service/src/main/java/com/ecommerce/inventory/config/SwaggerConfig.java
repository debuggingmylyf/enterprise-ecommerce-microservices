package com.ecommerce.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI configuration for the Inventory Service.
 *
 * <p>Exposes a custom {@link OpenAPI} bean that populates the Swagger UI
 * with service-specific metadata (title, version, contact).
 */
@Configuration
public class SwaggerConfig {

    /**
     * Builds and returns the {@link OpenAPI} descriptor for this service.
     *
     * @return configured {@link OpenAPI} instance consumed by Springdoc
     */
    @Bean
    public OpenAPI inventoryServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .version("v1")
                        .description("Enterprise E-Commerce – Inventory Service: manages stock levels, "
                                + "reservations, warehouse quantities, and low-stock monitoring.")
                        .contact(new Contact()
                                .name("Shubham")
                                .email("shubham.aj.kumar@capgemini.com"))
                );
    }
}
