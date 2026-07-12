package com.ecommerce.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for common date/time operations used across microservices.
 *
 * <p>All methods operate in UTC to ensure consistency across services that may
 * run in different time zones.
 */
public final class DateUtils {

    /** ISO 8601 formatter (UTC). */
    public static final DateTimeFormatter ISO_UTC =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    private DateUtils() {
        throw new UnsupportedOperationException("DateUtils is a utility class");
    }

    /**
     * Returns the current UTC timestamp.
     *
     * @return current {@link LocalDateTime} in UTC
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Returns the current UTC timestamp as an {@link Instant}.
     *
     * @return current instant
     */
    public static Instant nowInstant() {
        return Instant.now();
    }

    /**
     * Converts a {@link LocalDateTime} (assumed to be UTC) to epoch milliseconds.
     *
     * @param dateTime the UTC datetime; must not be {@code null}
     * @return epoch milliseconds
     */
    public static long toEpochMillis(final LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * Converts epoch milliseconds to a UTC {@link LocalDateTime}.
     *
     * @param epochMillis milliseconds since 1970-01-01T00:00:00Z
     * @return the corresponding UTC {@code LocalDateTime}
     */
    public static LocalDateTime fromEpochMillis(final long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
    }

    /**
     * Formats a {@link LocalDateTime} as an ISO 8601 UTC string
     * (e.g. {@code "2026-07-11T18:00:00Z"}).
     *
     * @param dateTime the UTC datetime; must not be {@code null}
     * @return formatted string
     */
    public static String formatIsoUtc(final LocalDateTime dateTime) {
        return ZonedDateTime.of(dateTime, ZoneOffset.UTC).format(ISO_UTC);
    }
}
