package com.ecommerce.product.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

/**
 * Request payload for updating an existing {@code Category}.
 *
 * <p><strong>Note:</strong> The filename contains a known typo ({@code Reqest} vs {@code Request}).
 * It is preserved intentionally to avoid breaking existing class references.
 */
@Data
public class UpdateCategoryReqest {

    /** New display name for the category; must not be blank. */
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    /** Optional long-form description. */
    private String description;

    /** Optional UUID of the new parent category; {@code null} makes it a root category. */
    private UUID parentCategoryId;
}

