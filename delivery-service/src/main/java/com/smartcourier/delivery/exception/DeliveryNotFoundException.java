package com.smartcourier.delivery.exception;

public class DeliveryNotFoundException extends RuntimeException {
    public DeliveryNotFoundException(String msg) {
        super(msg);
    }
}