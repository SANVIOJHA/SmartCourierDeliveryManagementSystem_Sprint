package com.smartcourier.delivery.entity;

import com.smartcourier.delivery.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique tracking number (UUID)
     */
    @Column(unique = true, nullable = false)
    private String trackingNumber;

    /**
     * Customer identity (injected from Gateway)
     */
    @Column(nullable = false)
    private String customerUsername;

    private String senderName;
    private String receiverName;

    private String source;
    private String destination;

    /**
     * ENUM instead of String → safer + validated
     */
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String assignedAgent;

    private Double price;
}