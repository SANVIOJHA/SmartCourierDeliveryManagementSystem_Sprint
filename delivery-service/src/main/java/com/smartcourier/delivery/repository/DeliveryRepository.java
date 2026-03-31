package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.Delivery;
import com.smartcourier.delivery.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findByAssignedAgent(String agent);

    List<Delivery> findByCustomerUsername(String username);

    List<Delivery> findByAssignedAgentIsNull();

    List<Delivery> findByPriceGreaterThanEqual(Double price);

    Optional<Delivery> findByTrackingNumber(String trackingNumber);

    long countByCustomerUsername(String username);

    long countByStatus(DeliveryStatus status);
}
