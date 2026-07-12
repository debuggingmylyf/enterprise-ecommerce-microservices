package com.ecommerce.common.util;

import java.util.UUID;

/**
 * Utility class for generating URL-safe slugs from arbitrary names.
 *
 * <p>A UUID suffix is appended to guarantee uniqueness even when
 * two items share the same display name.
 *
 * <p>This class is a pure static utility and must not be instantiated.
 *
 * <p>Algorithm:
 * <ol>
 *   <li>Trim and lowercase the input.</li>
 *   <li>Replace whitespace sequences with a single hyphen.</li>
 *   <li>Remove all characters that are not {@code [a-z0-9-]}.</li>
 *   <li>Collapse consecutive hyphens into one.</li>
 *   <li>Strip leading/trailing hyphens.</li>
 *   <li>Append the first 8 characters of a random UUID for uniqueness.</li>
 * </ol>
 */
public final class SlugGenerator {

    /** Regex matching any character that is not a lowercase letter, digit, or hyphen. */
    private static final String NON_SLUG_CHARS = "[^a-z0-9-]";

    /** Regex matching two or more consecutive hyphens. */
    private static final String CONSECUTIVE_HYPHENS = "-{2,}";

    private SlugGenerator() {
        throw new UnsupportedOperationException("SlugGenerator is a utility class");
    }

    /**
     * Generates a unique, URL-safe slug from the given name.
     *
     * @param name the raw name (e.g. a product name); must not be {@code null} or blank
     * @return a non-blank, unique URL slug
     * @throws IllegalArgumentException if {@code name} is {@code null} or blank
     */
    public static String generate(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }

        final String uniqueSuffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);

        final String base = name.trim()
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll(NON_SLUG_CHARS, "")
                .replaceAll(CONSECUTIVE_HYPHENS, "-")
                .replaceAll("^-|-$", "");

        return base + "-" + uniqueSuffix;
    }

    /**
     * Generates a deterministic (non-unique) slug from the given name.
     * Useful for testing or when the caller manages uniqueness externally.
     *
     * @param name the raw name; must not be {@code null} or blank
     * @return a URL-safe slug without a UUID suffix
     * @throws IllegalArgumentException if {@code name} is {@code null} or blank
     */
    public static String generateBase(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }

        return name.trim()
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll(NON_SLUG_CHARS, "")
                .replaceAll(CONSECUTIVE_HYPHENS, "-")
                .replaceAll("^-|-$", "");
    }
}
