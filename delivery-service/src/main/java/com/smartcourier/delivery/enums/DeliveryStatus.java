package com.smartcourier.delivery.enums;

/**
 * DeliveryStatus defines allowed lifecycle states
 * Prevents invalid string values in DB
 */
public enum DeliveryStatus {

    CREATED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}