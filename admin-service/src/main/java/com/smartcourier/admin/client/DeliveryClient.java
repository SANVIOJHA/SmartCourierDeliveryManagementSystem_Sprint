package com.smartcourier.admin.client;

import com.smartcourier.admin.dto.DeliverySummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/deliveries")
    List<DeliverySummaryDTO> getAllDeliveries();
}
