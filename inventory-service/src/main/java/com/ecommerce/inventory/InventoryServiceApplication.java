package com.ecommerce.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point for the Inventory Service.
 *
 * <p>Registers with the Eureka Discovery Server via {@link EnableDiscoveryClient}.
 * All remaining cross-cutting concerns (config server, JPA auditing, Swagger)
 * are wired through dedicated {@code @Configuration} classes in the
 * {@code com.ecommerce.inventory.config} package.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
