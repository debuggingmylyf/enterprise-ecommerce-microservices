package com.ecommerce.product.exception;

import com.ecommerce.common.exception.ErrorCode;

/**
 * Product-service–specific error codes.
 *
 * <p>Cross-cutting codes (e.g. {@link ErrorCode#RESOURCE_NOT_FOUND},
 * {@link ErrorCode#VALIDATION_ERROR}) live in the shared
 * {@link com.ecommerce.common.exception.ErrorCode} enum. Only codes that are
 * unique to this service belong here.
 */
public enum ProductErrorCode {

    /** A requested product could not be located. */
    PRODUCT_NOT_FOUND,

    /** A requested category could not be located. */
    CATEGORY_NOT_FOUND,

    /** A product with the given SKU already exists. */
    DUPLICATE_SKU,

    /** A product with the derived slug already exists. */
    DUPLICATE_SLUG,

    /** A category with the given name already exists. */
    DUPLICATE_CATEGORY_NAME,

    /** The supplied price range is invalid (e.g. min &gt; max). */
    INVALID_PRICE_RANGE,

    /** Inventory provisioning for a newly created product failed; manual provisioning required. */
    INVENTORY_PROVISIONING_FAILED
}
