package com.ecommerce.product.exception;

/**
 * Thrown when a requested product or category entity cannot be located.
 *
 * <p>Maps to HTTP {@code 404 Not Found} via the shared
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 *
 * <p>Usage:
 * <pre>{@code
 * throw new ResourceNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND,
 *         "Product with id " + id + " was not found");
 * }</pre>
 */
public class ResourceNotFoundException extends com.ecommerce.common.exception.ResourceNotFoundException {

    /**
     * Constructs a {@code ResourceNotFoundException} with a product-specific {@link ProductErrorCode}.
     *
     * @param errorCode product-domain error code
     * @param message   human-readable description of the missing resource
     */
    public ResourceNotFoundException(final ProductErrorCode errorCode, final String message) {
        super(errorCode.name(), message);
    }

    /**
     * Constructs a {@code ResourceNotFoundException} with only a message,
     * using the generic {@code RESOURCE_NOT_FOUND} code.
     *
     * @param message human-readable description
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
