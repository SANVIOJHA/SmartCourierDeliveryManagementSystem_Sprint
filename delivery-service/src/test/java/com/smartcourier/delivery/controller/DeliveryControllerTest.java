package com.smartcourier.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourier.delivery.dto.AgentAssignDTO;
import com.smartcourier.delivery.dto.DeliveryRequestDTO;
import com.smartcourier.delivery.dto.StatusUpdateDTO;
import com.smartcourier.delivery.entity.Delivery;
import com.smartcourier.delivery.enums.DeliveryStatus;
import com.smartcourier.delivery.exception.GlobalExceptionHandler;
import com.smartcourier.delivery.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {

    @Mock
    private DeliveryService deliveryService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new DeliveryController(deliveryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getMyDeliveriesShouldReturnCustomerDeliveries() throws Exception {
        when(deliveryService.getByCustomer("alice")).thenReturn(List.of(delivery(1L, "TRK1", DeliveryStatus.CREATED)));

        mockMvc.perform(get("/deliveries/my").header("X-User", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trackingNumber").value("TRK1"));
    }

    @Test
    void getMyDeliveryCountShouldReturnCount() throws Exception {
        when(deliveryService.countByCustomer("alice")).thenReturn(4L);

        mockMvc.perform(get("/deliveries/my/count").header("X-User", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4));
    }

    @Test
    void createShouldReturnCreatedDelivery() throws Exception {
        when(deliveryService.createDelivery(any(Delivery.class), eq("alice")))
                .thenReturn(delivery(1L, "TRK1", DeliveryStatus.CREATED));

        mockMvc.perform(post("/deliveries")
                        .header("X-User", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void getAllShouldReturnAllDeliveries() throws Exception {
        when(deliveryService.getAllDeliveries()).thenReturn(List.of(delivery(1L, "TRK1", DeliveryStatus.CREATED)));

        mockMvc.perform(get("/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getByIdShouldReturnDelivery() throws Exception {
        when(deliveryService.getDeliveryById(1L)).thenReturn(delivery(1L, "TRK1", DeliveryStatus.CREATED));

        mockMvc.perform(get("/deliveries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK1"));
    }

    @Test
    void getByTrackingNumberShouldReturnDelivery() throws Exception {
        when(deliveryService.getByTrackingNumber("TRK1")).thenReturn(delivery(1L, "TRK1", DeliveryStatus.CREATED));

        mockMvc.perform(get("/deliveries/tracking/TRK1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK1"));
    }

    @Test
    void updateShouldReturnUpdatedDelivery() throws Exception {
        when(deliveryService.updateDelivery(eq(1L), any(Delivery.class)))
                .thenReturn(delivery(1L, "TRK1", DeliveryStatus.CREATED));

        mockMvc.perform(put("/deliveries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/deliveries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Delivery deleted successfully"));
    }

    @Test
    void assignAgentShouldReturnAssignedDelivery() throws Exception {
        Delivery delivery = delivery(1L, "TRK1", DeliveryStatus.CREATED);
        delivery.setAssignedAgent("agent1");
        when(deliveryService.assignAgent(1L, "agent1")).thenReturn(delivery);

        mockMvc.perform(put("/deliveries/1/assign/agent1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedAgent").value("agent1"));
    }

    @Test
    void assignAgentWithBodyShouldReturnAssignedDelivery() throws Exception {
        AgentAssignDTO request = new AgentAssignDTO();
        request.setAgent("agent2");
        Delivery delivery = delivery(1L, "TRK1", DeliveryStatus.CREATED);
        delivery.setAssignedAgent("agent2");
        when(deliveryService.assignAgent(1L, "agent2")).thenReturn(delivery);

        mockMvc.perform(put("/deliveries/1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedAgent").value("agent2"));
    }

    @Test
    void updateStatusShouldReturnUpdatedStatus() throws Exception {
        StatusUpdateDTO request = new StatusUpdateDTO();
        request.setStatus("SHIPPED");
        request.setLocation("Hub");
        request.setDescription("Moved");
        when(deliveryService.updateStatus(1L, "SHIPPED", "Hub", "Moved"))
                .thenReturn(delivery(1L, "TRK1", DeliveryStatus.SHIPPED));

        mockMvc.perform(put("/deliveries/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void cancelShouldReturnCancelledDelivery() throws Exception {
        when(deliveryService.updateStatus(1L, "CANCELLED"))
                .thenReturn(delivery(1L, "TRK1", DeliveryStatus.CANCELLED));

        mockMvc.perform(put("/deliveries/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shipShouldReturnShippedDelivery() throws Exception {
        when(deliveryService.updateStatus(1L, "SHIPPED"))
                .thenReturn(delivery(1L, "TRK1", DeliveryStatus.SHIPPED));

        mockMvc.perform(put("/deliveries/1/ship"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void deliverShouldReturnDeliveredDelivery() throws Exception {
        when(deliveryService.updateStatus(1L, "DELIVERED"))
                .thenReturn(delivery(1L, "TRK1", DeliveryStatus.DELIVERED));

        mockMvc.perform(put("/deliveries/1/deliver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void getByStatusShouldReturnFilteredDeliveries() throws Exception {
        when(deliveryService.getByStatus("SHIPPED")).thenReturn(List.of(delivery(1L, "TRK1", DeliveryStatus.SHIPPED)));

        mockMvc.perform(get("/deliveries/status/SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SHIPPED"));
    }

    @Test
    void getByAgentShouldReturnAgentDeliveries() throws Exception {
        Delivery delivery = delivery(1L, "TRK1", DeliveryStatus.CREATED);
        delivery.setAssignedAgent("agent1");
        when(deliveryService.getByAgent("agent1")).thenReturn(List.of(delivery));

        mockMvc.perform(get("/deliveries/agent/agent1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assignedAgent").value("agent1"));
    }

    @Test
    void getUnassignedShouldReturnUnassignedDeliveries() throws Exception {
        when(deliveryService.getUnassigned()).thenReturn(List.of(delivery(1L, "TRK1", DeliveryStatus.CREATED)));

        mockMvc.perform(get("/deliveries/unassigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getByMinPriceShouldReturnDeliveriesAbovePrice() throws Exception {
        when(deliveryService.getByMinPrice(100.0)).thenReturn(List.of(delivery(1L, "TRK1", DeliveryStatus.CREATED)));

        mockMvc.perform(get("/deliveries/price/above/100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(499.0));
    }

    @Test
    void statusSummaryShouldReturnMap() throws Exception {
        when(deliveryService.getStatusSummary()).thenReturn(Map.of("CREATED", 2L));

        mockMvc.perform(get("/deliveries/summary/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CREATED").value(2));
    }

    @Test
    void countShouldReturnTotalCount() throws Exception {
        when(deliveryService.getAllDeliveries()).thenReturn(List.of(
                delivery(1L, "TRK1", DeliveryStatus.CREATED),
                delivery(2L, "TRK2", DeliveryStatus.SHIPPED)
        ));

        mockMvc.perform(get("/deliveries/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }

    @Test
    void searchShouldReturnStatusFilteredResults() throws Exception {
        when(deliveryService.getByStatus("CREATED")).thenReturn(List.of(delivery(1L, "TRK1", DeliveryStatus.CREATED)));

        mockMvc.perform(get("/deliveries/search").param("status", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CREATED"));
    }

    @Test
    void healthShouldReturnServiceMessage() throws Exception {
        mockMvc.perform(get("/deliveries/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Delivery Service is running"));
    }

    private DeliveryRequestDTO request() {
        DeliveryRequestDTO request = new DeliveryRequestDTO();
        request.setSenderName("Alice");
        request.setReceiverName("Bob");
        request.setSource("Chennai");
        request.setDestination("Bengaluru");
        request.setPrice(499.0);
        return request;
    }

    private Delivery delivery(Long id, String trackingNumber, DeliveryStatus status) {
        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setTrackingNumber(trackingNumber);
        delivery.setCustomerUsername("alice");
        delivery.setSenderName("Alice");
        delivery.setReceiverName("Bob");
        delivery.setSource("Chennai");
        delivery.setDestination("Bengaluru");
        delivery.setStatus(status);
        delivery.setPrice(499.0);
        return delivery;
    }
}
