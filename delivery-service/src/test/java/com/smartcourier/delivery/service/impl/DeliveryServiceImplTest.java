package com.smartcourier.delivery.service.impl;

import com.smartcourier.delivery.entity.Delivery;
import com.smartcourier.delivery.enums.DeliveryStatus;
import com.smartcourier.delivery.exception.InvalidStatusException;
import com.smartcourier.delivery.messaging.DeliveryEventPublisher;
import com.smartcourier.delivery.messaging.DeliveryStatusEvent;
import com.smartcourier.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryEventPublisher eventPublisher;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    @Test
    void createDeliveryShouldAssignDefaultsAndPublishEvent() {
        Delivery delivery = new Delivery();
        delivery.setSenderName("Alice");
        delivery.setReceiverName("Bob");
        delivery.setSource("Chennai");
        delivery.setDestination("Bengaluru");
        delivery.setPrice(499.0);

        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> {
            Delivery saved = invocation.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        Delivery saved = deliveryService.createDelivery(delivery, "alice");

        assertEquals("alice", saved.getCustomerUsername());
        assertEquals(DeliveryStatus.CREATED, saved.getStatus());
        assertNotNull(saved.getTrackingNumber());

        ArgumentCaptor<DeliveryStatusEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertEquals(101L, eventCaptor.getValue().getDeliveryId());
        assertEquals("CREATED", eventCaptor.getValue().getStatus());
    }

    @Test
    void updateStatusShouldRejectUnknownStatus() {
        Delivery delivery = new Delivery();
        delivery.setId(10L);
        delivery.setStatus(DeliveryStatus.CREATED);

        when(deliveryRepository.findById(10L)).thenReturn(Optional.of(delivery));

        InvalidStatusException exception = assertThrows(
                InvalidStatusException.class,
                () -> deliveryService.updateStatus(10L, "INVALID_STATUS", "Hub", "Bad status")
        );

        assertEquals("Invalid status value: INVALID_STATUS", exception.getMessage());
    }
}
