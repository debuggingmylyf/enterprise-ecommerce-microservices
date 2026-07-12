package com.ecommerce.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all domain events published across microservices.
 *
 * <p>When Kafka is introduced, event producers will extend this class and
 * serialise instances to JSON before publishing to a Kafka topic. Consumers
 * will deserialise back using the concrete subclass type.
 *
 * <p>Every event carries:
 * <ul>
 *   <li>A unique {@link #eventId} for deduplication and idempotency.</li>
 *   <li>An {@link #eventType} describing what happened.</li>
 *   <li>An {@link #occurredAt} timestamp in UTC.</li>
 *   <li>The {@link #sourceService} that emitted the event.</li>
 * </ul>
 *
 * <p>Example subclass:
 * <pre>{@code
 * @Getter
 * @SuperBuilder
 * @NoArgsConstructor
 * public class ProductCreatedEvent extends BaseEvent {
 *     private UUID productId;
 *     private String sku;
 * }
 * }</pre>
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEvent {

    /**
     * Globally unique identifier for this event instance.
     * Consumers use this to implement idempotent processing.
     */
    private UUID eventId;

    /** Discriminator describing what domain action occurred. */
    private EventType eventType;

    /** UTC timestamp when the event was created/emitted. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    /**
     * The name of the microservice that produced this event
     * (e.g. {@code "product-service"}, {@code "order-service"}).
     */
    private String sourceService;

    /**
     * Initialises the common audit fields. Call from subclass constructors
     * or factory methods before publishing.
     *
     * @param eventType     the type of this event
     * @param sourceService the name of the emitting service
     */
    protected void initEvent(final EventType eventType, final String sourceService) {
        this.eventId = UUID.randomUUID();
        this.eventType = eventType;
        this.occurredAt = LocalDateTime.now();
        this.sourceService = sourceService;
    }
}
