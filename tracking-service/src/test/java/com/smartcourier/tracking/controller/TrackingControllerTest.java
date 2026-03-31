package com.smartcourier.tracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartcourier.tracking.dto.TrackingDTO;
import com.smartcourier.tracking.entity.TrackingEvent;
import com.smartcourier.tracking.exception.GlobalExceptionHandler;
import com.smartcourier.tracking.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrackingControllerTest {

    @Mock
    private TrackingService trackingService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(new TrackingController(trackingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createShouldReturnSavedTrackingEvent() throws Exception {
        when(trackingService.addEvent(any(TrackingEvent.class))).thenReturn(event(1L, 10L, "CREATED"));

        mockMvc.perform(post("/tracking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto(10L, "CREATED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(10));
    }

    @Test
    void getShouldReturnTrackingHistory() throws Exception {
        when(trackingService.getByDeliveryId(10L)).thenReturn(List.of(event(1L, 10L, "CREATED")));

        mockMvc.perform(get("/tracking/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CREATED"));
    }

    @Test
    void latestShouldReturnMostRecentTrackingEvent() throws Exception {
        when(trackingService.getLatest(10L)).thenReturn(event(1L, 10L, "SHIPPED"));

        mockMvc.perform(get("/tracking/10/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void byStatusShouldReturnStatusFilteredEvents() throws Exception {
        when(trackingService.getByStatus("DELIVERED")).thenReturn(List.of(event(1L, 10L, "DELIVERED")));

        mockMvc.perform(get("/tracking/status/DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("DELIVERED"));
    }

    @Test
    void byLocationShouldReturnLocationFilteredEvents() throws Exception {
        when(trackingService.getByLocation("Hub")).thenReturn(List.of(event(1L, 10L, "CREATED")));

        mockMvc.perform(get("/tracking/location/Hub"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Hub"));
    }

    @Test
    void byRangeShouldReturnEventsInRange() throws Exception {
        when(trackingService.getByDeliveryIdAndRange(any(), any(), any())).thenReturn(List.of(event(1L, 10L, "CREATED")));

        mockMvc.perform(get("/tracking/10/range")
                        .param("from", "2026-03-31T10:00:00")
                        .param("to", "2026-03-31T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deliveryId").value(10));
    }

    @Test
    void countShouldReturnDeliveryEventCount() throws Exception {
        when(trackingService.countByDeliveryId(10L)).thenReturn(2L);

        mockMvc.perform(get("/tracking/10/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }

    @Test
    void existsShouldReturnTrueWhenEventsExist() throws Exception {
        when(trackingService.countByDeliveryId(10L)).thenReturn(1L);

        mockMvc.perform(get("/tracking/10/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void totalCountShouldReturnAllEventCount() throws Exception {
        when(trackingService.totalCount()).thenReturn(5L);

        mockMvc.perform(get("/tracking/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    void deleteShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/tracking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Deleted"));
    }

    @Test
    void healthShouldReturnServiceMessage() throws Exception {
        mockMvc.perform(get("/tracking/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Tracking Service Running"));
    }

    private TrackingDTO dto(Long deliveryId, String status) {
        TrackingDTO dto = new TrackingDTO();
        dto.setDeliveryId(deliveryId);
        dto.setStatus(status);
        dto.setLocation("Hub");
        dto.setDescription("Created");
        dto.setTimestamp(LocalDateTime.of(2026, 3, 31, 10, 0));
        return dto;
    }

    private TrackingEvent event(Long id, Long deliveryId, String status) {
        TrackingEvent event = new TrackingEvent();
        event.setId(id);
        event.setDeliveryId(deliveryId);
        event.setStatus(status);
        event.setLocation("Hub");
        event.setDescription("Created");
        event.setTimestamp(LocalDateTime.of(2026, 3, 31, 10, 0));
        return event;
    }
}
