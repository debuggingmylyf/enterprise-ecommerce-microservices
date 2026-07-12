package com.ecommerce.product.util;

/**
 * @deprecated Use {@link com.ecommerce.common.util.SlugGenerator} instead.
 *             This class is kept for backward compatibility and delegates all calls
 *             to the shared implementation.
 */
@Deprecated(since = "1.0.0-SNAPSHOT", forRemoval = true)
public final class SlugGenerator {

    private SlugGenerator() {
        throw new UnsupportedOperationException("SlugGenerator is a utility class");
    }

    /**
     * @deprecated Use {@link com.ecommerce.common.util.SlugGenerator#generate(String)} instead.
     */
    @Deprecated(since = "1.0.0-SNAPSHOT", forRemoval = true)
    public static String generate(final String name) {
        return com.ecommerce.common.util.SlugGenerator.generate(name);
    }
}
