package com.ecommerce.order.service;

import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.order.dto.request.CreateOrderRequest;
import com.ecommerce.order.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.dto.response.CreateOrderResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing orders.
 */
public interface OrderService {

    /**
     * Creates a new order, validates products, and reserves inventory.
     *
     * @param request the order creation request
     * @param userId  the UUID of the user placing the order
     * @return the created order response
     */
    CreateOrderResponse createOrder(CreateOrderRequest request, UUID userId);

    /**
     * Retrieves a detailed order by its UUID.
     *
     * @param id       the UUID of the order
     * @param userId   the requesting user's UUID
     * @param userRole the requesting user's role (e.g. ROLE_CUSTOMER or ROLE_ADMIN)
     * @return the detailed order response
     */
    OrderResponse getOrderById(UUID id, UUID userId, String userRole);

    /**
     * Retrieves all orders for the currently authenticated user.
     *
     * @param userId the UUID of the user
     * @return a list of order summaries
     */
    List<OrderSummaryResponse> getMyOrders(UUID userId);

    /**
     * Cancels an order and releases its inventory reservations.
     *
     * @param id       the UUID of the order to cancel
     * @param userId   the requesting user's UUID
     * @param userRole the requesting user's role
     * @return the updated order response
     */
    OrderResponse cancelOrder(UUID id, UUID userId, String userRole);

    /**
     * Updates an order's status (Admin only).
     *
     * @param id      the UUID of the order
     * @param request the updated status request payload
     * @return the updated order response
     */
    OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request);

    /**
     * Retrieves all orders in the system with pagination (Admin only).
     *
     * @param pageable pagination options
     * @return a paginated list of order summaries
     */
    PaginatedResponse<OrderSummaryResponse> getAllOrders(Pageable pageable);

    /**
     * Internal callback to mark an order payment as successful.
     *
     * @param id the UUID of the order
     * @return the updated order response
     */
    OrderResponse markPaymentSuccess(UUID id);

    /**
     * Internal callback to mark an order payment as failed.
     *
     * @param id the UUID of the order
     * @return the updated order response
     */
    OrderResponse markPaymentFailed(UUID id);
}
