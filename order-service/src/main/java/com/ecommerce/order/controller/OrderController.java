package com.ecommerce.order.controller;

import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.order.dto.request.CreateOrderRequest;
import com.ecommerce.order.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.dto.response.CreateOrderResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.dto.response.OrderSummaryResponse;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing endpoints for placing, retrieving, and managing orders.
 *
 * <p>Base path: {@code /api/v1/orders}</p>
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates/places a new order.
     *
     * @param userIdHeader the authenticated user's ID forwarded from gateway
     * @param request      the validated order creation payload
     * @return {@code 201 Created} with order registration details
     */
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestHeader(name = "X-User-Id") final String userIdHeader,
            @Valid @RequestBody final CreateOrderRequest request) {

        log.info("POST /api/v1/orders – placing new order for user {}", userIdHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final CreateOrderResponse response = orderService.createOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves an order by its ID.
     * Accessible by the order owner or users with admin roles.
     *
     * @param id             the UUID of the order
     * @param userIdHeader   the authenticated user's ID
     * @param userRoleHeader the authenticated user's role
     * @return {@code 200 OK} with the detailed order information
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable final UUID id,
            @RequestHeader(name = "X-User-Id") final String userIdHeader,
            @RequestHeader(name = "X-User-Role") final String userRoleHeader) {

        log.debug("GET /api/v1/orders/{} – request from user {} with role {}", id, userIdHeader, userRoleHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final OrderResponse response = orderService.getOrderById(id, userId, userRoleHeader);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all orders belonging to the currently authenticated user.
     *
     * @param userIdHeader the authenticated user's ID
     * @return {@code 200 OK} with a list of user orders
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderSummaryResponse>> getMyOrders(
            @RequestHeader(name = "X-User-Id") final String userIdHeader) {

        log.debug("GET /api/v1/orders/my-orders – fetching orders for user {}", userIdHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final List<OrderSummaryResponse> response = orderService.getMyOrders(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels an order.
     * Accessible by the order owner or users with admin roles.
     *
     * @param id             the UUID of the order
     * @param userIdHeader   the authenticated user's ID
     * @param userRoleHeader the authenticated user's role
     * @return {@code 200 OK} with the cancelled order response
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable final UUID id,
            @RequestHeader(name = "X-User-Id") final String userIdHeader,
            @RequestHeader(name = "X-User-Role") final String userRoleHeader) {

        log.info("PATCH /api/v1/orders/{}/cancel – request from user {} with role {}", id, userIdHeader, userRoleHeader);
        final UUID userId = UUID.fromString(userIdHeader);
        final OrderResponse response = orderService.cancelOrder(id, userId, userRoleHeader);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    // Admin Endpoints
    // -------------------------------------------------------------------------

    /**
     * Returns a paginated list of all orders in the system.
     *
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 20)
     * @param sortBy    field to sort by (default: {@code createdAt})
     * @param direction sort direction, {@code ASC} or {@code DESC} (default: {@code DESC})
     * @return {@code 200 OK} with a paginated envelope of order summaries
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<OrderSummaryResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(defaultValue = "createdAt") final String sortBy,
            @RequestParam(defaultValue = "DESC") final String direction) {

        log.info("GET /api/v1/orders – page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        final Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);
        final PaginatedResponse<OrderSummaryResponse> response = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an order's status (Admin only).
     *
     * @param id      the UUID of the order
     * @param request the status update payload
     * @return {@code 200 OK} with the updated order response
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable final UUID id,
            @Valid @RequestBody final UpdateOrderStatusRequest request) {

        log.info("PATCH /api/v1/orders/{}/status – updating status to {}", id, request.getStatus());
        final OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
