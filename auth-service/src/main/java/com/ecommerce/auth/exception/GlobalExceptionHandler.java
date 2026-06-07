package com.ecommerce.auth.exception;

import com.ecommerce.auth.constants.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExist(UserAlreadyExistException ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXITS, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpired(TokenExpiredException ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_EXPIRED, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS, "Invalid email or password", request);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        ApiError error = buildError(
                HttpStatus.CONFLICT,
                ErrorCode.DATA_INTEGRITY_VIOLATION,
                "Request conflicts with existing data",
                request
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiError apiError = buildError(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, errors, request);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiError buildError(HttpStatus status, ErrorCode errorCode, Object message, HttpServletRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}
