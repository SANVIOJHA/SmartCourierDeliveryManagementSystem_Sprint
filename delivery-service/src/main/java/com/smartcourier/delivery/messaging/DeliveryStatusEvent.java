package com.smartcourier.delivery.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusEvent {
    private Long deliveryId;
    private String trackingNumber;
    private String status;
    private String location;
    private String description;
    private LocalDateTime eventTime;
}
