package com.ecommerce.product.exception;

/**
 * Thrown when a domain business rule specific to the Product Service is violated
 * (e.g. duplicate SKU, invalid price range, deactivated product operation).
 *
 * <p>Maps to HTTP {@code 422 Unprocessable Entity} via the shared
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 *
 * <p>Usage:
 * <pre>{@code
 * throw new BusinessException(ProductErrorCode.DUPLICATE_SKU, "SKU 'X' already exists");
 * }</pre>
 */
public class BusinessException extends com.ecommerce.common.exception.BusinessException {

    /**
     * Constructs a {@code BusinessException} with a product-specific {@link ProductErrorCode}.
     *
     * @param errorCode product-domain error code
     * @param message   human-readable description of the violated rule
     */
    public BusinessException(final ProductErrorCode errorCode, final String message) {
        super(errorCode.name(), message);
    }
}
