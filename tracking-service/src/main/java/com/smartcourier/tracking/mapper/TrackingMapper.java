package com.smartcourier.tracking.mapper;

import com.smartcourier.tracking.dto.TrackingDTO;
import com.smartcourier.tracking.entity.TrackingEvent;

public final class TrackingMapper {

    private TrackingMapper() {
    }

    public static TrackingEvent toEntity(TrackingDTO dto) {
        TrackingEvent event = new TrackingEvent();
        event.setDeliveryId(dto.getDeliveryId());
        event.setStatus(dto.getStatus());
        event.setLocation(dto.getLocation());
        event.setDescription(dto.getDescription());
        event.setTimestamp(dto.getTimestamp());
        return event;
    }

    public static TrackingDTO toDto(TrackingEvent event) {
        TrackingDTO dto = new TrackingDTO();
        dto.setDeliveryId(event.getDeliveryId());
        dto.setStatus(event.getStatus());
        dto.setLocation(event.getLocation());
        dto.setDescription(event.getDescription());
        dto.setTimestamp(event.getTimestamp());
        return dto;
    }
}
