package com.ecommerce.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Generic success envelope used by all microservices.
 *
 * <p>Example usage in a controller:
 * <pre>{@code
 * return ResponseEntity.ok(ApiResponse.success("Product created", response));
 * }</pre>
 *
 * @param <T> the type of the payload data
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Whether the request succeeded. Always {@code true} for this wrapper. */
    private final boolean success;

    /** Human-readable summary message. */
    private final String message;

    /** The actual response payload; may be {@code null} for void operations. */
    private final T data;

    /** Server-side timestamp at response creation time. */
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a successful response with data and a message.
     *
     * @param message short description of the outcome
     * @param data    the response payload
     * @param <T>     payload type
     * @return a fully populated {@code ApiResponse}
     */
    public static <T> ApiResponse<T> success(final String message, final T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with data and a default "Success" message.
     *
     * @param data the response payload
     * @param <T>  payload type
     * @return a fully populated {@code ApiResponse}
     */
    public static <T> ApiResponse<T> success(final T data) {
        return success("Success", data);
    }

    /**
     * Creates a successful response with no payload (e.g. for DELETE operations).
     *
     * @param message short description of the outcome
     * @return a {@code ApiResponse} with {@code null} data
     */
    public static ApiResponse<Void> ok(final String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
