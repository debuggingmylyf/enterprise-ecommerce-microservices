package com.ecommerce.order.service.serviceImpl;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.PaginatedResponse;
import com.ecommerce.order.client.InventoryServiceClient;
import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.dto.*;
import com.ecommerce.order.dto.request.CreateOrderRequest;
import com.ecommerce.order.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.dto.response.CreateOrderResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.dto.response.OrderSummaryResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.enums.PaymentStatus;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link OrderService} interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(final CreateOrderRequest request, final UUID userId) {
        log.info("Creating order for user: {}", userId);

        final List<OrderItem> orderItems = new ArrayList<>();
        final List<ReserveInventoryRequest> successfulReservations = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        try {
            for (final var itemRequest : request.getItems()) {
                final UUID productId = itemRequest.getProductId();
                final int quantity = itemRequest.getQuantity();

                // 1. Validate Product via Feign Client
                final ResponseEntity<ProductClientResponse> productRes = productServiceClient.getProductById(productId);
                if (productRes == null || productRes.getBody() == null) {
                    throw new ResourceNotFoundException("PRODUCT_NOT_FOUND",
                            "Product not found or unable to validate: " + productId);
                }
                final ProductClientResponse product = productRes.getBody();

                // 2. Check Inventory Availability via Feign Client
                final ResponseEntity<InventoryAvailabilityResponse> availRes = inventoryServiceClient.checkAvailability(productId);
                if (availRes == null || availRes.getBody() == null || !availRes.getBody().isAvailable()) {
                    throw new BusinessException("INSUFFICIENT_STOCK",
                            "Product is out of stock: " + productId);
                }

                final InventoryAvailabilityResponse availability = availRes.getBody();
                if (availability.getAvailableQuantity() < quantity) {
                    throw new BusinessException("INSUFFICIENT_STOCK",
                            "Insufficient stock for product: " + productId + ". Available: " + availability.getAvailableQuantity());
                }

                final String warehouseCode = availability.getWarehouseCode();

                // 3. Reserve Stock
                final ReserveInventoryRequest reserveReq = ReserveInventoryRequest.builder()
                        .productId(productId)
                        .warehouseCode(warehouseCode)
                        .quantity(quantity)
                        .build();

                final ResponseEntity<Object> reserveRes = inventoryServiceClient.reserveStock(reserveReq);
                if (reserveRes == null || !reserveRes.getStatusCode().is2xxSuccessful()) {
                    throw new BusinessException("INSUFFICIENT_STOCK",
                            "Failed to reserve stock for product: " + productId);
                }

                // Track successful reservations for potential rollback
                successfulReservations.add(reserveReq);

                // 4. Calculate pricing
                final BigDecimal unitPrice = product.getPricing().getDiscountPrice() != null
                        ? product.getPricing().getDiscountPrice()
                        : product.getPricing().getBasePrice();

                final BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                totalAmount = totalAmount.add(subtotal);

                // Build OrderItem entity
                final OrderItem orderItem = OrderItem.builder()
                        .productId(productId)
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .subtotal(subtotal)
                        .warehouseCode(warehouseCode)
                        .build();

                orderItems.add(orderItem);
            }

            // 5. Generate Order Number & Save Order
            final String orderNumber = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

            final Order order = Order.builder()
                    .orderNumber(orderNumber)
                    .userId(userId)
                    .totalAmount(totalAmount)
                    .status(OrderStatus.CREATED)
                    .paymentStatus(PaymentStatus.PENDING)
                    .shippingName(request.getShippingAddress().getShippingName())
                    .shippingPhone(request.getShippingAddress().getShippingPhone())
                    .shippingAddressLine1(request.getShippingAddress().getAddressLine1())
                    .shippingAddressLine2(request.getShippingAddress().getAddressLine2())
                    .city(request.getShippingAddress().getCity())
                    .state(request.getShippingAddress().getState())
                    .country(request.getShippingAddress().getCountry())
                    .postalCode(request.getShippingAddress().getPostalCode())
                    .build();

            for (final OrderItem item : orderItems) {
                order.addItem(item);
            }

            final Order savedOrder = orderRepository.save(order);
            log.info("Order created successfully: {}", savedOrder.getOrderNumber());
            return orderMapper.toCreateOrderResponse(savedOrder);

        } catch (final Exception ex) {
            log.error("Error creating order, triggering distributed rollback compensation: {}", ex.getMessage());
            // Compensating Transaction (Distributed Rollback)
            for (final var reservation : successfulReservations) {
                try {
                    final ReleaseInventoryRequest releaseReq = ReleaseInventoryRequest.builder()
                            .productId(reservation.getProductId())
                            .warehouseCode(reservation.getWarehouseCode())
                            .quantity(reservation.getQuantity())
                            .build();
                    inventoryServiceClient.releaseStock(releaseReq);
                } catch (final Exception rollbackEx) {
                    log.error("Failed to release reserved stock during compensating rollback for product {}: {}",
                            reservation.getProductId(), rollbackEx.getMessage());
                }
            }
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(final UUID id, final UUID userId, final String userRole) {
        log.debug("Fetching order: {}", id);
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + id));

        // Enforce security role check
        if (!userRole.contains("ADMIN") && !order.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "Not authorized to view this order");
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getMyOrders(final UUID userId) {
        log.debug("Fetching orders for user: {}", userId);
        final List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(orderMapper::toOrderSummaryResponse).toList();
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(final UUID id, final UUID userId, final String userRole) {
        log.info("Cancelling order: {}", id);
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + id));

        // Security check
        if (!userRole.contains("ADMIN") && !order.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "Not authorized to cancel this order");
        }

        // Status check
        if (order.getStatus() == OrderStatus.CANCELLED ||
                order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Order cannot be cancelled in its current state: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Compensating transaction: Release stock
        for (final OrderItem item : order.getItems()) {
            final ReleaseInventoryRequest releaseReq = ReleaseInventoryRequest.builder()
                    .productId(item.getProductId())
                    .warehouseCode(item.getWarehouseCode())
                    .quantity(item.getQuantity())
                    .build();
            inventoryServiceClient.releaseStock(releaseReq);
        }

        final Order savedOrder = orderRepository.save(order);
        log.info("Order {} cancelled successfully", savedOrder.getOrderNumber());
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(final UUID id, final UpdateOrderStatusRequest request) {
        log.info("Updating order status for order: {} to {}", id, request.getStatus());
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + id));

        final OrderStatus oldStatus = order.getStatus();
        final OrderStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            return orderMapper.toOrderResponse(order);
        }

        // Validate state transitions
        if (newStatus == OrderStatus.CANCELLED) {
            return cancelOrder(id, order.getUserId(), "ROLE_ADMIN");
        }

        if (newStatus == OrderStatus.SHIPPED && oldStatus != OrderStatus.CONFIRMED) {
            throw new BusinessException("INVALID_ORDER_STATUS", "Order must be CONFIRMED before shipping");
        }

        if (newStatus == OrderStatus.DELIVERED && oldStatus != OrderStatus.SHIPPED) {
            throw new BusinessException("INVALID_ORDER_STATUS", "Order must be SHIPPED before delivery");
        }

        if (newStatus == OrderStatus.CONFIRMED && oldStatus != OrderStatus.CREATED) {
            throw new BusinessException("INVALID_ORDER_STATUS", "Order must be in CREATED state to confirm");
        }

        order.setStatus(newStatus);
        final Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<OrderSummaryResponse> getAllOrders(final Pageable pageable) {
        log.debug("Fetching all orders paginated");
        final Page<Order> orderPage = orderRepository.findAll(pageable);
        final Page<OrderSummaryResponse> summaryPage = orderPage.map(orderMapper::toOrderSummaryResponse);

        final String sortBy = pageable.getSort().isSorted()
                ? pageable.getSort().iterator().next().getProperty()
                : "createdAt";
        final String sortDirection = pageable.getSort().isSorted()
                ? pageable.getSort().iterator().next().getDirection().name()
                : "DESC";

        return PaginatedResponse.of(summaryPage, sortBy, sortDirection);
    }

    @Override
    @Transactional
    public OrderResponse markPaymentSuccess(final UUID id) {
        log.info("Processing successful payment for order: {}", id);
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + id));

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return orderMapper.toOrderResponse(order);
        }

        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setStatus(OrderStatus.CONFIRMED);

        // Confirm inventory reservation permanently
        for (final OrderItem item : order.getItems()) {
            final ConfirmInventoryRequest confirmReq = ConfirmInventoryRequest.builder()
                    .productId(item.getProductId())
                    .warehouseCode(item.getWarehouseCode())
                    .quantity(item.getQuantity())
                    .build();
            inventoryServiceClient.confirmStock(confirmReq);
        }

        final Order savedOrder = orderRepository.save(order);
        log.info("Order {} confirmed and paid successfully", savedOrder.getOrderNumber());
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse markPaymentFailed(final UUID id) {
        log.info("Processing failed payment callback for order: {}", id);
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + id));

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("INVALID_ORDER_STATUS", "Payment already success, cannot mark failed");
        }

        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setStatus(OrderStatus.CANCELLED);

        // Release inventory reservation
        for (final OrderItem item : order.getItems()) {
            final ReleaseInventoryRequest releaseReq = ReleaseInventoryRequest.builder()
                    .productId(item.getProductId())
                    .warehouseCode(item.getWarehouseCode())
                    .quantity(item.getQuantity())
                    .build();
            inventoryServiceClient.releaseStock(releaseReq);
        }

        final Order savedOrder = orderRepository.save(order);
        log.info("Order {} marked cancelled due to payment failure", savedOrder.getOrderNumber());
        return orderMapper.toOrderResponse(savedOrder);
    }
}
