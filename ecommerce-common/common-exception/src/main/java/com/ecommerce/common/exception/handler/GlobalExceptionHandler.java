package com.ecommerce.common.exception.handler;

import com.ecommerce.common.exception.AccessDeniedException;
import com.ecommerce.common.exception.BaseException;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ErrorCode;
import com.ecommerce.common.exception.InvalidTokenException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.TokenExpiredException;
import com.ecommerce.common.exception.UserAlreadyExistsException;
import com.ecommerce.common.response.ApiErrorResponse;
import com.ecommerce.common.response.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Centralized exception handler for all microservices.
 *
 * <p>Maps the shared exception hierarchy and common Spring/JPA exceptions to
 * consistent {@link ApiErrorResponse} payloads. Service teams can either:
 * <ul>
 *   <li>Use this handler as-is (activated automatically via Spring Boot
 *       auto-configuration when on the classpath), or</li>
 *   <li>Extend it in their own {@code @RestControllerAdvice} to add
 *       service-specific handlers.</li>
 * </ul>
 *
 * <p>HTTP status mapping:
 * <table border="1">
 *   <tr><th>Exception</th><th>HTTP Status</th></tr>
 *   <tr><td>{@link ResourceNotFoundException}</td><td>404</td></tr>
 *   <tr><td>{@link BusinessException}</td><td>422</td></tr>
 *   <tr><td>{@link UserAlreadyExistsException}</td><td>409</td></tr>
 *   <tr><td>{@link AccessDeniedException}</td><td>403</td></tr>
 *   <tr><td>{@link InvalidTokenException}</td><td>401</td></tr>
 *   <tr><td>{@link TokenExpiredException}</td><td>401</td></tr>
 *   <tr><td>{@link MethodArgumentNotValidException}</td><td>400</td></tr>
 *   <tr><td>{@link DataIntegrityViolationException}</td><td>409</td></tr>
 *   <tr><td>{@link Exception}</td><td>500</td></tr>
 * </table>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // -------------------------------------------------------------------------
    // 404 — Not Found
    // -------------------------------------------------------------------------

    /**
     * Handles {@link ResourceNotFoundException}.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 404 Not Found}
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            final ResourceNotFoundException ex,
            final HttpServletRequest request) {

        log.warn("Resource not found [path={}]: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex, request, null);
    }

    // -------------------------------------------------------------------------
    // 409 — Conflict
    // -------------------------------------------------------------------------

    /**
     * Handles {@link UserAlreadyExistsException}.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 409 Conflict}
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(
            final UserAlreadyExistsException ex,
            final HttpServletRequest request) {

        log.warn("User conflict [path={}]: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.CONFLICT, ex, request, null);
    }

    /**
     * Handles Spring Data {@link DataIntegrityViolationException} (unique-constraint violations).
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 409 Conflict}
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            final DataIntegrityViolationException ex,
            final HttpServletRequest request) {

        log.error("Data integrity violation [path={}]: {}", request.getRequestURI(), ex.getMessage());
        ApiErrorResponse body = buildError(
                HttpStatus.CONFLICT,
                ErrorCode.DATA_INTEGRITY_VIOLATION.name(),
                "Request conflicts with existing data",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // -------------------------------------------------------------------------
    // 422 — Unprocessable Entity (business rule violations)
    // -------------------------------------------------------------------------

    /**
     * Handles {@link BusinessException}.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 422 Unprocessable Entity}
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            final BusinessException ex,
            final HttpServletRequest request) {

        log.warn("Business rule violation [path={}, code={}]: {}",
                request.getRequestURI(), ex.getResponseErrorCode(), ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex, request, null);
    }

    // -------------------------------------------------------------------------
    // 401 — Unauthorized
    // -------------------------------------------------------------------------

    /**
     * Handles {@link TokenExpiredException}.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 401 Unauthorized}
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleTokenExpired(
            final TokenExpiredException ex,
            final HttpServletRequest request) {

        log.warn("Token expired [path={}]: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, ex, request, null);
    }

    /**
     * Handles {@link InvalidTokenException}.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 401 Unauthorized}
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidToken(
            final InvalidTokenException ex,
            final HttpServletRequest request) {

        log.warn("Invalid token [path={}]: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, ex, request, null);
    }

    // -------------------------------------------------------------------------
    // 403 — Forbidden
    // -------------------------------------------------------------------------

    /**
     * Handles {@link AccessDeniedException}.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 403 Forbidden}
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            final AccessDeniedException ex,
            final HttpServletRequest request) {

        log.warn("Access denied [path={}]: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.FORBIDDEN, ex, request, null);
    }

    // -------------------------------------------------------------------------
    // 400 — Bad Request (bean-validation)
    // -------------------------------------------------------------------------

    /**
     * Handles bean-validation failures from {@code @Valid}-annotated request bodies.
     * Collects per-field violation messages into a list of {@link FieldError}.
     *
     * @param ex      the Spring MVC validation exception
     * @param request the current HTTP request
     * @return {@code 400 Bad Request} with per-field error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {

        log.warn("Validation failed [path={}]: {} field error(s)",
                request.getRequestURI(), ex.getBindingResult().getFieldErrorCount());

        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        ApiErrorResponse body = buildError(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.name(),
                "Request validation failed",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    // -------------------------------------------------------------------------
    // 500 — Internal Server Error (catch-all)
    // -------------------------------------------------------------------------

    /**
     * Catch-all handler for any unhandled {@link Exception}.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 500 Internal Server Error}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            final Exception ex,
            final HttpServletRequest request) {

        log.error("Unhandled exception [path={}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiErrorResponse body = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a {@link ResponseEntity} from a {@link BaseException}.
     * The {@code errorCode} in the response is taken from
     * {@link BaseException#getResponseErrorCode()} so service-local codes are surfaced.
     */
    private ResponseEntity<ApiErrorResponse> build(
            final HttpStatus status,
            final BaseException ex,
            final HttpServletRequest request,
            final List<FieldError> fieldErrors) {

        ApiErrorResponse body = buildError(status, ex.getResponseErrorCode(),
                ex.getMessage(), request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Constructs an {@link ApiErrorResponse} with all fields populated.
     */
    private ApiErrorResponse buildError(
            final HttpStatus status,
            final String errorCode,
            final Object message,
            final String path,
            final List<FieldError> fieldErrors) {

        return ApiErrorResponse.builder()
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors)
                .build();
    }
}
