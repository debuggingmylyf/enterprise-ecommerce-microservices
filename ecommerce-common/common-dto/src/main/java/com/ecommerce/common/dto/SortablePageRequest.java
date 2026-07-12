package com.ecommerce.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reusable pagination and sorting request DTO.
 *
 * <p>Controllers can accept this as a {@code @ModelAttribute} or inline its fields
 * as {@code @RequestParam}s and build an instance manually. The {@code PageableUtils}
 * helper in {@code common-util} converts this to a Spring {@code Pageable}.
 *
 * <pre>{@code
 * @GetMapping
 * public ResponseEntity<PaginatedResponse<ProductResponse>> list(
 *         @ModelAttribute @Valid SortablePageRequest pageRequest) {
 *     Pageable pageable = PageableUtils.toPageable(pageRequest);
 *     ...
 * }
 * }</pre>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortablePageRequest {

    /** Zero-based page index. Defaults to {@code 0}. */
    @Min(value = 0, message = "Page index must be 0 or greater")
    @Builder.Default
    private int page = 0;

    /** Number of items per page. Defaults to {@code 20}, maximum {@code 100}. */
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Builder.Default
    private int size = 20;

    /** Field name to sort by. Defaults to {@code "createdAt"}. */
    @Builder.Default
    private String sortBy = "createdAt";

    /**
     * Sort direction: {@code ASC} or {@code DESC} (case-insensitive).
     * Defaults to {@code "DESC"}.
     */
    @Pattern(regexp = "(?i)ASC|DESC", message = "Sort direction must be ASC or DESC")
    @Builder.Default
    private String sortDirection = "DESC";
}
