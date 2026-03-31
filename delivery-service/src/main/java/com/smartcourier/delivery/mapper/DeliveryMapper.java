package com.smartcourier.delivery.mapper;

import com.smartcourier.delivery.dto.*;
import com.smartcourier.delivery.entity.Delivery;

public class DeliveryMapper {

    public static Delivery toEntity(DeliveryRequestDTO dto) {
        Delivery d = new Delivery();
        d.setSenderName(dto.getSenderName());
        d.setReceiverName(dto.getReceiverName());
        d.setSource(dto.getSource());
        d.setDestination(dto.getDestination());
        d.setPrice(dto.getPrice());
        return d;
    }

    public static DeliveryResponseDTO toDTO(Delivery d) {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();

        dto.setId(d.getId());
        dto.setTrackingNumber(d.getTrackingNumber());

        dto.setSenderName(d.getSenderName());
        dto.setReceiverName(d.getReceiverName());

        dto.setSource(d.getSource());
        dto.setDestination(d.getDestination());

        dto.setStatus(d.getStatus() != null ? d.getStatus().name() : null);

        dto.setAssignedAgent(d.getAssignedAgent());
        dto.setPrice(d.getPrice());

        return dto;
    }
}
