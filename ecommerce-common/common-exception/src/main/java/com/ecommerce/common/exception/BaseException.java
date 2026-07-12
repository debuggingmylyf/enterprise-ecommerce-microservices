package com.ecommerce.common.exception;

/**
 * Abstract base class for all domain exceptions in the ecommerce platform.
 *
 * <p>Every service-specific exception should extend this class and supply
 * an {@link ErrorCode} (from the common enum or the service's own enum passed
 * as a {@code String} code via the overloaded constructor).
 *
 * <pre>{@code
 * public class ProductNotFoundException extends BaseException {
 *     public ProductNotFoundException(UUID id) {
 *         super(ErrorCode.RESOURCE_NOT_FOUND, "Product " + id + " not found");
 *     }
 * }
 * }</pre>
 */
public abstract class BaseException extends RuntimeException {

    /** The cross-cutting error code. */
    private final ErrorCode errorCode;

    /**
     * The service-specific error code string, used when a service delegates to a
     * local {@code ErrorCode} enum and wants to surface its name in the response.
     * May be {@code null} — in that case {@link #errorCode} is used.
     */
    private final String localErrorCode;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Constructs a base exception with a cross-cutting {@link ErrorCode}.
     *
     * @param errorCode the common error code
     * @param message   human-readable description
     */
    protected BaseException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
        this.localErrorCode = null;
    }

    /**
     * Constructs a base exception with a service-local error code string.
     * Use this when the service has its own {@code ErrorCode} enum and wants to
     * surface it without depending on the common one.
     *
     * @param localErrorCode the service-specific code name (e.g. "PRODUCT_NOT_FOUND")
     * @param message        human-readable description
     * @param fallback       a cross-cutting code used for HTTP-status mapping
     */
    protected BaseException(
            final String localErrorCode,
            final String message,
            final ErrorCode fallback) {
        super(message);
        this.errorCode = fallback;
        this.localErrorCode = localErrorCode;
    }

    /**
     * Constructs a base exception with a cross-cutting code and a root cause.
     *
     * @param errorCode the common error code
     * @param message   human-readable description
     * @param cause     the underlying exception
     */
    protected BaseException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.localErrorCode = null;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * Returns the cross-cutting {@link ErrorCode} used for HTTP status mapping.
     *
     * @return the error code; never {@code null}
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the code name to surface in the API response.
     * If a service-local code was provided it takes precedence; otherwise the
     * name of {@link #errorCode} is returned.
     *
     * @return a non-null error code string suitable for {@code ApiErrorResponse.errorCode}
     */
    public String getResponseErrorCode() {
        return localErrorCode != null ? localErrorCode : errorCode.name();
    }
}
