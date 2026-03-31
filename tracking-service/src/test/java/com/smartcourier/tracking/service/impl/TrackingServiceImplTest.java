package com.smartcourier.tracking.service.impl;

import com.smartcourier.tracking.entity.TrackingEvent;
import com.smartcourier.tracking.exception.TrackingNotFoundException;
import com.smartcourier.tracking.repository.TrackingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackingServiceImplTest {

    @Mock
    private TrackingRepository trackingRepository;

    @InjectMocks
    private TrackingServiceImpl trackingService;

    @Test
    void addEventShouldAssignTimestampWhenMissing() {
        // RabbitMQ-driven events may arrive without a timestamp and should still be persisted safely.
        TrackingEvent event = new TrackingEvent();
        event.setDeliveryId(100L);
        event.setStatus("CREATED");

        when(trackingRepository.save(any(TrackingEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrackingEvent saved = trackingService.addEvent(event);

        assertNotNull(saved.getTimestamp());
        assertEquals(100L, saved.getDeliveryId());
    }

    @Test
    void getLatestShouldReturnMostRecentTrackingEvent() {
        // The latest tracking endpoint should expose the newest event for a delivery.
        TrackingEvent older = new TrackingEvent();
        older.setTimestamp(LocalDateTime.of(2026, 3, 31, 10, 0));
        TrackingEvent newer = new TrackingEvent();
        newer.setTimestamp(LocalDateTime.of(2026, 3, 31, 11, 0));

        when(trackingRepository.findByDeliveryId(55L)).thenReturn(List.of(older, newer));

        TrackingEvent latest = trackingService.getLatest(55L);

        assertEquals(newer.getTimestamp(), latest.getTimestamp());
    }

    @Test
    void getLatestShouldThrowWhenNoTrackingEventsExist() {
        // Consumers should get a clear domain error when a delivery has no tracking history yet.
        when(trackingRepository.findByDeliveryId(55L)).thenReturn(List.of());

        TrackingNotFoundException exception = assertThrows(
                TrackingNotFoundException.class,
                () -> trackingService.getLatest(55L)
        );

        assertEquals("No tracking events found for delivery id: 55", exception.getMessage());
    }
}
