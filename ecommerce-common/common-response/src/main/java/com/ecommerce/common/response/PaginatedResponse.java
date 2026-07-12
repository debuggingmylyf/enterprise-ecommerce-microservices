package com.ecommerce.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Generic paginated response envelope for collection endpoints.
 *
 * <p>Wrap any list of items with consistent pagination metadata:
 * <pre>{@code
 * return ResponseEntity.ok(PaginatedResponse.of(page));
 * }</pre>
 *
 * @param <T> the type of items in the page
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse<T> {

    /** The items on the current page. */
    private final List<T> content;

    /** Zero-based current page index. */
    private final int page;

    /** Requested page size. */
    private final int size;

    /** Total number of elements across all pages. */
    private final long totalElements;

    /** Total number of pages. */
    private final int totalPages;

    /** Whether this is the last page. */
    private final boolean last;

    /** Field used to sort the results. */
    private final String sortBy;

    /** Sort direction ({@code ASC} or {@code DESC}). */
    private final String sortDirection;

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a {@code PaginatedResponse} from a Spring Data {@link org.springframework.data.domain.Page}.
     *
     * @param page          the Spring Data page
     * @param sortBy        the sort field that was applied
     * @param sortDirection the sort direction that was applied
     * @param <T>           the element type
     * @return a populated {@code PaginatedResponse}
     */
    public static <T> PaginatedResponse<T> of(
            final org.springframework.data.domain.Page<T> page,
            final String sortBy,
            final String sortDirection) {

        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
    }
}
