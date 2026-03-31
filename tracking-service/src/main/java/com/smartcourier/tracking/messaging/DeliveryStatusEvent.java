package com.smartcourier.tracking.messaging;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryStatusEvent {
    private Long deliveryId;
    private String trackingNumber;
    private String status;
    private String location;
    private String description;
    private LocalDateTime eventTime;
}
