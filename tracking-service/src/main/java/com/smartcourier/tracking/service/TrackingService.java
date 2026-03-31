package com.smartcourier.tracking.service;

import com.smartcourier.tracking.entity.TrackingEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface TrackingService {

    TrackingEvent addEvent(TrackingEvent event);

    List<TrackingEvent> getByDeliveryId(Long deliveryId);

    TrackingEvent getLatest(Long deliveryId);

    List<TrackingEvent> getByStatus(String status);

    List<TrackingEvent> getByLocation(String location);

    List<TrackingEvent> getByDeliveryIdAndRange(Long deliveryId, LocalDateTime from, LocalDateTime to);

    long countByDeliveryId(Long deliveryId);

    long totalCount();

    void delete(Long id);
}
