package com.smartcourier.delivery.controller;

import com.smartcourier.delivery.dto.AgentAssignDTO;
import com.smartcourier.delivery.dto.DeliveryRequestDTO;
import com.smartcourier.delivery.dto.DeliveryResponseDTO;
import com.smartcourier.delivery.dto.StatusUpdateDTO;
import com.smartcourier.delivery.entity.Delivery;
import com.smartcourier.delivery.mapper.DeliveryMapper;
import com.smartcourier.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    @GetMapping("/my")
    public List<DeliveryResponseDTO> getMyDeliveries(@RequestHeader("X-User") String username) {
        return service.getByCustomer(username).stream().map(DeliveryMapper::toDTO).toList();
    }

    @GetMapping("/my/count")
    public long getMyDeliveryCount(@RequestHeader("X-User") String username) {
        return service.countByCustomer(username);
    }

    @PostMapping
    public DeliveryResponseDTO create(
            @Valid @RequestBody DeliveryRequestDTO dto,
            @RequestHeader("X-User") String username) {
        Delivery delivery = DeliveryMapper.toEntity(dto);
        return DeliveryMapper.toDTO(service.createDelivery(delivery, username));
    }

    @GetMapping
    public List<DeliveryResponseDTO> getAll() {
        return service.getAllDeliveries().stream().map(DeliveryMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public DeliveryResponseDTO getById(@PathVariable Long id) {
        return DeliveryMapper.toDTO(service.getDeliveryById(id));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public DeliveryResponseDTO getByTrackingNumber(@PathVariable String trackingNumber) {
        return DeliveryMapper.toDTO(service.getByTrackingNumber(trackingNumber));
    }

    @PutMapping("/{id}")
    public DeliveryResponseDTO update(@PathVariable Long id, @Valid @RequestBody DeliveryRequestDTO dto) {
        return DeliveryMapper.toDTO(service.updateDelivery(id, DeliveryMapper.toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteDelivery(id);
        return "Delivery deleted successfully";
    }

    @PutMapping("/{id}/assign/{agent}")
    public DeliveryResponseDTO assignAgent(@PathVariable Long id, @PathVariable String agent) {
        return DeliveryMapper.toDTO(service.assignAgent(id, agent));
    }

    @PutMapping("/{id}/assign")
    public DeliveryResponseDTO assignAgentWithBody(@PathVariable Long id, @Valid @RequestBody AgentAssignDTO dto) {
        return DeliveryMapper.toDTO(service.assignAgent(id, dto.getAgent()));
    }

    @PutMapping("/{id}/status")
    public DeliveryResponseDTO updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO dto) {
        return DeliveryMapper.toDTO(
                service.updateStatus(id, dto.getStatus(), dto.getLocation(), dto.getDescription())
        );
    }

    @PutMapping("/{id}/cancel")
    public DeliveryResponseDTO cancel(@PathVariable Long id) {
        return DeliveryMapper.toDTO(service.updateStatus(id, "CANCELLED"));
    }

    @PutMapping("/{id}/ship")
    public DeliveryResponseDTO ship(@PathVariable Long id) {
        return DeliveryMapper.toDTO(service.updateStatus(id, "SHIPPED"));
    }

    @PutMapping("/{id}/deliver")
    public DeliveryResponseDTO deliver(@PathVariable Long id) {
        return DeliveryMapper.toDTO(service.updateStatus(id, "DELIVERED"));
    }

    @GetMapping("/status/{status}")
    public List<DeliveryResponseDTO> getByStatus(@PathVariable String status) {
        return service.getByStatus(status).stream().map(DeliveryMapper::toDTO).toList();
    }

    @GetMapping("/agent/{agent}")
    public List<DeliveryResponseDTO> getByAgent(@PathVariable String agent) {
        return service.getByAgent(agent).stream().map(DeliveryMapper::toDTO).toList();
    }

    @GetMapping("/unassigned")
    public List<DeliveryResponseDTO> getUnassigned() {
        return service.getUnassigned().stream().map(DeliveryMapper::toDTO).toList();
    }

    @GetMapping("/price/above/{amount}")
    public List<DeliveryResponseDTO> getByMinPrice(@PathVariable Double amount) {
        return service.getByMinPrice(amount).stream().map(DeliveryMapper::toDTO).toList();
    }

    @GetMapping("/summary/status")
    public Map<String, Long> statusSummary() {
        return service.getStatusSummary();
    }

    @GetMapping("/count")
    public int count() {
        return service.getAllDeliveries().size();
    }

    @GetMapping("/search")
    public List<DeliveryResponseDTO> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String agent) {
        if (status != null && !status.isBlank()) {
            return service.getByStatus(status).stream().map(DeliveryMapper::toDTO).toList();
        }
        if (agent != null && !agent.isBlank()) {
            return service.getByAgent(agent).stream().map(DeliveryMapper::toDTO).toList();
        }
        return service.getAllDeliveries().stream().map(DeliveryMapper::toDTO).toList();
    }

    @GetMapping("/health")
    public String health() {
        return "Delivery Service is running";
    }
}
