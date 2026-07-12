package com.ecommerce.auth.constants;

/**
 * Auth-service–specific error codes.
 *
 * <p>Cross-cutting codes (e.g. {@link com.ecommerce.common.exception.ErrorCode#VALIDATION_ERROR},
 * {@link com.ecommerce.common.exception.ErrorCode#DATA_INTEGRITY_VIOLATION}) live in the shared
 * {@link com.ecommerce.common.exception.ErrorCode} enum. Only codes unique to this service
 * belong here.
 */
public enum ErrorCode {

    USER_NOT_FOUND,

    USER_ALREADY_EXISTS,

    TOKEN_EXPIRED,

    INVALID_TOKEN,

    INVALID_CREDENTIALS,

    ACCESS_DENIED,

    INTERNAL_SERVER_ERROR,

    VALIDATION_ERROR,

    DATA_INTEGRITY_VIOLATION,

    RESOURCE_NOT_FOUND;
}
