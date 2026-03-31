package com.smartcourier.delivery.service;

import com.smartcourier.delivery.entity.Delivery;

import java.util.List;
import java.util.Map;

public interface DeliveryService {

    Delivery createDelivery(Delivery d, String username);

    List<Delivery> getAllDeliveries();

    Delivery getDeliveryById(Long id);

    Delivery updateDelivery(Long id, Delivery d);

    void deleteDelivery(Long id);

    Delivery assignAgent(Long id, String agent);

    Delivery updateStatus(Long id, String status);

    Delivery updateStatus(Long id, String status, String location, String description);

    List<Delivery> getByStatus(String status);

    List<Delivery> getByAgent(String agent);

    List<Delivery> getByCustomer(String username);

    Delivery getByTrackingNumber(String trackingNumber);

    List<Delivery> getUnassigned();

    List<Delivery> getByMinPrice(Double minPrice);

    long countByCustomer(String username);

    Map<String, Long> getStatusSummary();
}
