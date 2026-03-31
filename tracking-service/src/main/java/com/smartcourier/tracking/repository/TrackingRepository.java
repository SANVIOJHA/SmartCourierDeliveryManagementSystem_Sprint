package com.smartcourier.tracking.repository;

import com.smartcourier.tracking.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrackingRepository extends JpaRepository<TrackingEvent, Long> {

    List<TrackingEvent> findByDeliveryId(Long deliveryId);

    List<TrackingEvent> findByStatus(String status);

    List<TrackingEvent> findByLocationContainingIgnoreCase(String location);

    List<TrackingEvent> findByDeliveryIdAndTimestampBetween(Long deliveryId, LocalDateTime from, LocalDateTime to);

    long countByDeliveryId(Long deliveryId);
}
