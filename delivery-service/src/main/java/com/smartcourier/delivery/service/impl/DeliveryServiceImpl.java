package com.smartcourier.delivery.service.impl;

import com.smartcourier.delivery.entity.Delivery;
import com.smartcourier.delivery.enums.DeliveryStatus;
import com.smartcourier.delivery.exception.DeliveryNotFoundException;
import com.smartcourier.delivery.exception.InvalidStatusException;
import com.smartcourier.delivery.messaging.DeliveryEventPublisher;
import com.smartcourier.delivery.messaging.DeliveryStatusEvent;
import com.smartcourier.delivery.repository.DeliveryRepository;
import com.smartcourier.delivery.service.DeliveryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repo;
    private final DeliveryEventPublisher eventPublisher;

    public DeliveryServiceImpl(DeliveryRepository repo, DeliveryEventPublisher eventPublisher) {
        this.repo = repo;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Delivery createDelivery(Delivery delivery, String username) {
        delivery.setCustomerUsername(username);
        delivery.setTrackingNumber(UUID.randomUUID().toString());
        delivery.setStatus(DeliveryStatus.CREATED);

        Delivery saved = repo.save(delivery);
        publishStatusEvent(saved, "Pickup request created");
        return saved;
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        return repo.findAll();
    }

    @Override
    public Delivery getDeliveryById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found for id: " + id));
    }

    @Override
    public Delivery updateDelivery(Long id, Delivery delivery) {
        Delivery existing = getDeliveryById(id);
        existing.setSenderName(delivery.getSenderName());
        existing.setReceiverName(delivery.getReceiverName());
        existing.setSource(delivery.getSource());
        existing.setDestination(delivery.getDestination());
        existing.setPrice(delivery.getPrice());
        return repo.save(existing);
    }

    @Override
    public void deleteDelivery(Long id) {
        Delivery existing = getDeliveryById(id);
        repo.delete(existing);
    }

    @Override
    public Delivery assignAgent(Long id, String agent) {
        Delivery delivery = getDeliveryById(id);
        delivery.setAssignedAgent(agent);
        Delivery saved = repo.save(delivery);
        publishStatusEvent(saved, "Agent assigned: " + agent);
        return saved;
    }

    @Override
    public Delivery updateStatus(Long id, String status) {
        return updateStatus(id, status, "Transit hub", "Status updated via API");
    }

    @Override
    public Delivery updateStatus(Long id, String status, String location, String description) {
        Delivery delivery = getDeliveryById(id);

        try {
            delivery.setStatus(DeliveryStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Invalid status value: " + status);
        }

        Delivery saved = repo.save(delivery);
        publishStatusEvent(saved, location, description);
        return saved;
    }

    @Override
    public List<Delivery> getByStatus(String status) {
        try {
            DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());
            return repo.findByStatus(deliveryStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Invalid status value: " + status);
        }
    }

    @Override
    public List<Delivery> getByAgent(String agent) {
        return repo.findByAssignedAgent(agent);
    }

    @Override
    public List<Delivery> getByCustomer(String username) {
        return repo.findByCustomerUsername(username);
    }

    @Override
    public Delivery getByTrackingNumber(String trackingNumber) {
        return repo.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found for tracking number: " + trackingNumber));
    }

    @Override
    public List<Delivery> getUnassigned() {
        return repo.findByAssignedAgentIsNull();
    }

    @Override
    public List<Delivery> getByMinPrice(Double minPrice) {
        return repo.findByPriceGreaterThanEqual(minPrice);
    }

    @Override
    public long countByCustomer(String username) {
        return repo.countByCustomerUsername(username);
    }

    @Override
    public Map<String, Long> getStatusSummary() {
        Map<String, Long> summary = new HashMap<>();
        for (DeliveryStatus status : DeliveryStatus.values()) {
            summary.put(status.name(), repo.countByStatus(status));
        }
        return summary;
    }

    private void publishStatusEvent(Delivery delivery, String description) {
        publishStatusEvent(delivery, "Transit hub", description);
    }

    private void publishStatusEvent(Delivery delivery, String location, String description) {
        DeliveryStatusEvent event = new DeliveryStatusEvent(
                delivery.getId(),
                delivery.getTrackingNumber(),
                delivery.getStatus().name(),
                location,
                description,
                LocalDateTime.now()
        );
        eventPublisher.publish(event);
    }
}
