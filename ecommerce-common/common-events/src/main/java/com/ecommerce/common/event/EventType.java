package com.ecommerce.common.event;

/**
 * Catalogue of domain event types for the ecommerce platform.
 *
 * <p>Used as the discriminator in {@link BaseEvent#getEventType()} so that
 * Kafka consumers can route or filter messages without deserialising the full payload.
 *
 * <p>When adding a new event, follow the naming convention:
 * <pre>{@code <ENTITY>_<VERB_PAST_TENSE>}</pre>
 *
 * <p><strong>Note:</strong> This enum is intentionally forward-looking. Unused values
 * are placeholders for services not yet implemented. Do not remove values without a
 * coordinated migration — consumers may rely on the enum name for routing.
 */
public enum EventType {

    // ------------------------------------------------------------------
    // Product Service
    // ------------------------------------------------------------------

    /** A new product has been created (in DRAFT status). */
    PRODUCT_CREATED,

    /** An existing product's core fields have been updated. */
    PRODUCT_UPDATED,

    /** A product's lifecycle status has changed (e.g. DRAFT → ACTIVE). */
    PRODUCT_STATUS_CHANGED,

    /** A product has been soft-deleted. */
    PRODUCT_DELETED,

    // ------------------------------------------------------------------
    // Inventory Service
    // ------------------------------------------------------------------

    /** Initial inventory was provisioned for a new product. */
    INVENTORY_PROVISIONED,

    /** Stock quantity has been updated (restock or adjustment). */
    INVENTORY_UPDATED,

    /** Stock for a product has been reserved for an order. */
    INVENTORY_RESERVED,

    /** A reservation has been released (e.g. order cancelled). */
    INVENTORY_RESERVATION_RELEASED,

    /** Stock has dropped to or below the configured low-stock threshold. */
    INVENTORY_LOW_STOCK,

    /** Stock has reached zero. */
    INVENTORY_OUT_OF_STOCK,

    // ------------------------------------------------------------------
    // Order Service (future)
    // ------------------------------------------------------------------

    /** A new order has been placed. */
    ORDER_PLACED,

    /** An order has been confirmed (payment captured). */
    ORDER_CONFIRMED,

    /** An order has been shipped. */
    ORDER_SHIPPED,

    /** An order has been delivered. */
    ORDER_DELIVERED,

    /** An order has been cancelled. */
    ORDER_CANCELLED,

    // ------------------------------------------------------------------
    // Payment Service (future)
    // ------------------------------------------------------------------

    /** A payment has been initiated. */
    PAYMENT_INITIATED,

    /** A payment has been successfully completed. */
    PAYMENT_COMPLETED,

    /** A payment has failed. */
    PAYMENT_FAILED,

    /** A refund has been processed. */
    PAYMENT_REFUNDED,

    // ------------------------------------------------------------------
    // User / Auth Service (future)
    // ------------------------------------------------------------------

    /** A new user account has been registered. */
    USER_REGISTERED,

    /** A user's profile has been updated. */
    USER_UPDATED,

    /** A user account has been deactivated. */
    USER_DEACTIVATED,

    // ------------------------------------------------------------------
    // Notification Service (future)
    // ------------------------------------------------------------------

    /** An email notification should be sent. */
    NOTIFICATION_EMAIL_REQUESTED,

    /** A push notification should be sent. */
    NOTIFICATION_PUSH_REQUESTED
}
