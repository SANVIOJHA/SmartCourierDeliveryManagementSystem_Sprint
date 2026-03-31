package com.smartcourier.tracking.messaging;

import com.smartcourier.tracking.config.RabbitMqConfig;
import com.smartcourier.tracking.entity.TrackingEvent;
import com.smartcourier.tracking.service.TrackingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStatusEventListener {

    private final TrackingService trackingService;

    public DeliveryStatusEventListener(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @RabbitListener(queues = RabbitMqConfig.TRACKING_QUEUE)
    public void consume(DeliveryStatusEvent event) {
        TrackingEvent trackingEvent = new TrackingEvent();
        trackingEvent.setDeliveryId(event.getDeliveryId());
        trackingEvent.setStatus(event.getStatus());
        trackingEvent.setLocation(event.getLocation() != null ? event.getLocation() : "Transit hub");
        trackingEvent.setDescription(event.getDescription() != null ? event.getDescription() : "Event received from delivery-service");
        trackingEvent.setTimestamp(event.getEventTime());
        trackingService.addEvent(trackingEvent);
    }
}
