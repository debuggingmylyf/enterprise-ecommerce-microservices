package com.ecommerce.product.dto.response.product;

import com.ecommerce.product.enums.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Lightweight response returned upon successful product creation,
 * containing only the identifiers, initial status, and inventory provisioning
 * outcome — consumers use {@code GET /api/v1/products/{id}} for the full representation.
 */
@Getter
@Builder
public class CreateProductResponse {

    private UUID productId;

    private String skuCode;

    private ProductStatus status;

    /** Human-readable summary of the overall creation outcome. */
    private String message;

    /**
     * Status of the automatic inventory provisioning attempt.
     *
     * <ul>
     *   <li>{@code "PROVISIONED"} – inventory record created in WH-DEFAULT.</li>
     *   <li>{@code "DEFERRED"} – inventory-service was unreachable; manual provisioning required.</li>
     * </ul>
     */
    private String inventoryStatus;
}