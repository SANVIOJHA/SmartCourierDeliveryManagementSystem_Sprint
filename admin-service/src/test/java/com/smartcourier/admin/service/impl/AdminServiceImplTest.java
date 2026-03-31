package com.smartcourier.admin.service.impl;

import com.smartcourier.admin.client.DeliveryClient;
import com.smartcourier.admin.dto.AdminDashboardDTO;
import com.smartcourier.admin.dto.DeliverySummaryDTO;
import com.smartcourier.admin.entity.Report;
import com.smartcourier.admin.exception.ResourceNotFoundException;
import com.smartcourier.admin.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void getDashboardShouldAggregateDeliveryStatuses() {
        // Dashboard counters should reflect the status mix returned by the Feign client.
        when(deliveryClient.getAllDeliveries()).thenReturn(List.of(
                deliveryWithStatus("SHIPPED"),
                deliveryWithStatus("DELIVERED"),
                deliveryWithStatus("DELIVERED"),
                deliveryWithStatus("CANCELLED")
        ));

        AdminDashboardDTO dashboard = adminService.getDashboard();

        assertEquals(4, dashboard.getTotalDeliveries());
        assertEquals(1, dashboard.getShipped());
        assertEquals(2, dashboard.getDelivered());
        assertEquals(1, dashboard.getCancelled());
    }

    @Test
    void getLatestReportShouldThrowWhenNoReportsExist() {
        // Empty report history should surface a domain exception instead of returning null.
        when(reportRepository.findAll()).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                adminService::getLatestReport
        );

        assertEquals("No reports available", exception.getMessage());
    }

    private DeliverySummaryDTO deliveryWithStatus(String status) {
        DeliverySummaryDTO dto = new DeliverySummaryDTO();
        dto.setStatus(status);
        return dto;
    }
}
