package com.ecommerce.order.enums;

public enum OrderStatus {

    CREATED, // Order is created but not confirmed yet
    CONFIRMED, // Order is confirmed
    CANCELLED, // Order is cancelled
    SHIPPED, // Order is shipped
    DELIVERED // Order is delivered
}
