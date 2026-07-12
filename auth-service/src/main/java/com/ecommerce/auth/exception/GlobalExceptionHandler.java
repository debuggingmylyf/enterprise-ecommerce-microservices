package com.ecommerce.auth.exception;

import com.ecommerce.auth.constants.ErrorCode;
import com.ecommerce.common.response.ApiErrorResponse;
import com.ecommerce.common.response.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Auth-service exception handler.
 *
 * <p>Extends the shared {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}
 * and adds auth-specific handlers for:
 * <ul>
 *   <li>{@link UserAlreadyExistException} → 409</li>
 *   <li>{@link InvalidTokenException} → 401</li>
 *   <li>{@link TokenExpiredException} → 401</li>
 *   <li>{@link BadCredentialsException} → 401</li>
 * </ul>
 *
 * <p>The common handler covers:
 * <ul>
 *   <li>{@link com.ecommerce.common.exception.ResourceNotFoundException} → 404</li>
 *   <li>{@link com.ecommerce.common.exception.BusinessException} → 422</li>
 *   <li>{@link org.springframework.web.bind.MethodArgumentNotValidException} → 400</li>
 *   <li>{@link org.springframework.dao.DataIntegrityViolationException} → 409</li>
 *   <li>{@link Exception} → 500</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler
        extends com.ecommerce.common.exception.handler.GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // Auth-specific handlers
    // -------------------------------------------------------------------------

    /**
     * Handles {@link UserAlreadyExistException} → 409 Conflict.
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExist(
            final UserAlreadyExistException ex,
            final HttpServletRequest request) {

        return buildAuthError(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS, ex.getMessage(), request);
    }

    /**
     * Handles {@link InvalidTokenException} → 401 Unauthorized.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidToken(
            final InvalidTokenException ex,
            final HttpServletRequest request) {

        return buildAuthError(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN, ex.getMessage(), request);
    }

    /**
     * Handles {@link TokenExpiredException} → 401 Unauthorized.
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleTokenExpired(
            final TokenExpiredException ex,
            final HttpServletRequest request) {

        return buildAuthError(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_EXPIRED, ex.getMessage(), request);
    }

    /**
     * Handles Spring Security's {@link BadCredentialsException} → 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            final BadCredentialsException ex,
            final HttpServletRequest request) {

        return buildAuthError(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS,
                "Invalid email or password", request);
    }

    /**
     * Override the common validation handler to also collect field errors consistently.
     */
    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .message(fieldErrors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // -------------------------------------------------------------------------
    // Private helper
    // -------------------------------------------------------------------------

    private ResponseEntity<ApiErrorResponse> buildAuthError(
            final HttpStatus status,
            final ErrorCode errorCode,
            final Object message,
            final HttpServletRequest request) {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(status.value())
                .errorCode(errorCode.name())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
