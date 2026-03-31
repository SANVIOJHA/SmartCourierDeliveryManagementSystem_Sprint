package com.smartcourier.tracking.exception;

public class TrackingNotFoundException extends RuntimeException {
    public TrackingNotFoundException(String message) {
        super(message);
    }
}