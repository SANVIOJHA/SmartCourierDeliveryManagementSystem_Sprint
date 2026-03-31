package com.smartcourier.delivery.dto;

import lombok.Data;

/**
 * Response sent to client
 */
@Data
public class DeliveryResponseDTO {

    private Long id;
    private String trackingNumber;

    private String senderName;
    private String receiverName;

    private String source;
    private String destination;

    private String status;

    private String assignedAgent;

    private Double price;
}