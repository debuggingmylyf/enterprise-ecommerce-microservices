package com.ecommerce.common.security;

/**
 * HTTP security constants shared across all microservices.
 *
 * <p>Centralises header names, token prefixes, and JWT claim keys so that
 * every service reads from the same source of truth instead of hardcoding strings.
 *
 * <pre>{@code
 * String token = request.getHeader(SecurityConstants.AUTH_HEADER);
 * if (token != null && token.startsWith(SecurityConstants.BEARER_PREFIX)) {
 *     String jwt = token.substring(SecurityConstants.BEARER_PREFIX.length());
 * }
 * }</pre>
 */
public final class SecurityConstants {

    // -------------------------------------------------------------------------
    // HTTP headers
    // -------------------------------------------------------------------------

    /** Standard HTTP Authorization header name. */
    public static final String AUTH_HEADER = "Authorization";

    /** Bearer token prefix (includes the trailing space). */
    public static final String BEARER_PREFIX = "Bearer ";

    /** Custom header used by the API Gateway to forward the authenticated user's ID. */
    public static final String X_USER_ID_HEADER = "X-User-Id";

    /** Custom header used by the API Gateway to forward the authenticated user's email. */
    public static final String X_USER_EMAIL_HEADER = "X-User-Email";

    /** Custom header used by the API Gateway to forward the authenticated user's role. */
    public static final String X_USER_ROLE_HEADER = "X-User-Role";

    // -------------------------------------------------------------------------
    // JWT claim keys
    // -------------------------------------------------------------------------

    /** JWT claim key for the subject (username / email). */
    public static final String JWT_SUBJECT_CLAIM = "sub";

    /** JWT claim key for the user's role(s). */
    public static final String JWT_ROLES_CLAIM = "roles";

    /** JWT claim key that discriminates access tokens from refresh tokens. */
    public static final String JWT_TOKEN_TYPE_CLAIM = "tokenType";

    /** Value of {@link #JWT_TOKEN_TYPE_CLAIM} for access tokens. */
    public static final String JWT_ACCESS_TOKEN_TYPE = "ACCESS";

    /** Value of {@link #JWT_TOKEN_TYPE_CLAIM} for refresh tokens. */
    public static final String JWT_REFRESH_TOKEN_TYPE = "REFRESH";

    // -------------------------------------------------------------------------
    // Public API paths (used in security config to permit without auth)
    // -------------------------------------------------------------------------

    /** Prefix for all public authentication endpoints. */
    public static final String AUTH_PATH_PREFIX = "/api/v1/auth/**";

    /** Actuator health endpoint, always public. */
    public static final String ACTUATOR_HEALTH_PATH = "/actuator/health";

    /** Swagger / OpenAPI paths, typically public in non-production. */
    public static final String SWAGGER_UI_PATH    = "/swagger-ui/**";
    public static final String OPENAPI_DOCS_PATH  = "/v3/api-docs/**";

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private SecurityConstants() {
        throw new UnsupportedOperationException("SecurityConstants is a constants class");
    }
}
