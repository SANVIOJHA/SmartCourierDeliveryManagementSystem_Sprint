package com.smartcourier.tracking.controller;

import com.smartcourier.tracking.dto.TrackingDTO;
import com.smartcourier.tracking.entity.TrackingEvent;
import com.smartcourier.tracking.mapper.TrackingMapper;
import com.smartcourier.tracking.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    private final TrackingService service;

    public TrackingController(TrackingService service) {
        this.service = service;
    }

    @PostMapping
    public TrackingDTO create(@Valid @RequestBody TrackingDTO dto) {
        TrackingEvent saved = service.addEvent(TrackingMapper.toEntity(dto));
        return TrackingMapper.toDto(saved);
    }

    @GetMapping("/{deliveryId}")
    public List<TrackingDTO> get(@PathVariable Long deliveryId) {
        return service.getByDeliveryId(deliveryId).stream().map(TrackingMapper::toDto).toList();
    }

    @GetMapping("/{deliveryId}/latest")
    public TrackingDTO latest(@PathVariable Long deliveryId) {
        return TrackingMapper.toDto(service.getLatest(deliveryId));
    }

    @GetMapping("/status/{status}")
    public List<TrackingDTO> byStatus(@PathVariable String status) {
        return service.getByStatus(status).stream().map(TrackingMapper::toDto).toList();
    }

    @GetMapping("/location/{location}")
    public List<TrackingDTO> byLocation(@PathVariable String location) {
        return service.getByLocation(location).stream().map(TrackingMapper::toDto).toList();
    }

    @GetMapping("/{deliveryId}/range")
    public List<TrackingDTO> byRange(
            @PathVariable Long deliveryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return service.getByDeliveryIdAndRange(deliveryId, from, to).stream()
                .map(TrackingMapper::toDto)
                .toList();
    }

    @GetMapping("/{deliveryId}/count")
    public long count(@PathVariable Long deliveryId) {
        return service.countByDeliveryId(deliveryId);
    }

    @GetMapping("/{deliveryId}/exists")
    public boolean exists(@PathVariable Long deliveryId) {
        return service.countByDeliveryId(deliveryId) > 0;
    }

    @GetMapping("/count")
    public long totalCount() {
        return service.totalCount();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted";
    }

    @GetMapping("/health")
    public String health() {
        return "Tracking Service Running";
    }
}
