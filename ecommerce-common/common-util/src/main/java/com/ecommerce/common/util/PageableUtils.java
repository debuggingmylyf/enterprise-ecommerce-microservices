package com.ecommerce.common.util;

import com.ecommerce.common.dto.SortablePageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for building Spring Data {@link Pageable} instances from
 * common pagination parameters.
 *
 * <p>Eliminates the repetitive {@code PageRequest.of(...)} boilerplate in
 * every controller.
 */
public final class PageableUtils {

    private PageableUtils() {
        throw new UnsupportedOperationException("PageableUtils is a utility class");
    }

    /**
     * Converts a {@link SortablePageRequest} to a Spring {@link Pageable}.
     *
     * @param request the validated pagination request; must not be {@code null}
     * @return a configured {@code Pageable}
     */
    public static Pageable toPageable(final SortablePageRequest request) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(request.getSortDirection()),
                request.getSortBy()
        );
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    /**
     * Builds a {@link Pageable} directly from individual parameters.
     *
     * @param page          zero-based page index (clamped to {@code >= 0})
     * @param size          page size (clamped to range {@code [1, 100]})
     * @param sortBy        field name to sort by
     * @param sortDirection {@code "ASC"} or {@code "DESC"} (case-insensitive)
     * @return a configured {@code Pageable}
     */
    public static Pageable toPageable(
            final int page,
            final int size,
            final String sortBy,
            final String sortDirection) {

        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 100);

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy
        );
        return PageRequest.of(safePage, safeSize, sort);
    }
}
