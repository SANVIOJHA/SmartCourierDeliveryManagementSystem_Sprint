package com.smartcourier.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartcourier.admin.dto.AdminDashboardDTO;
import com.smartcourier.admin.dto.ReportDTO;
import com.smartcourier.admin.entity.Report;
import com.smartcourier.admin.exception.GlobalExceptionHandler;
import com.smartcourier.admin.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
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
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(adminService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createShouldReturnSavedReport() throws Exception {
        when(adminService.generateReport(any(Report.class))).thenReturn(report(1L, "DAILY"));

        mockMvc.perform(post("/admin/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportType").value("DAILY"));
    }

    @Test
    void getAllShouldReturnAllReports() throws Exception {
        when(adminService.getAllReports()).thenReturn(List.of(report(1L, "DAILY")));

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getShouldReturnReportById() throws Exception {
        when(adminService.getReport(1L)).thenReturn(report(1L, "DAILY"));

        mockMvc.perform(get("/admin/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportType").value("DAILY"));
    }

    @Test
    void updateShouldReturnUpdatedReport() throws Exception {
        when(adminService.updateReport(eq(1L), any(ReportDTO.class))).thenReturn(report(1L, "MONTHLY"));

        mockMvc.perform(put("/admin/reports/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportType").value("MONTHLY"));
    }

    @Test
    void deleteShouldReturnDeletedMessage() throws Exception {
        mockMvc.perform(delete("/admin/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Deleted"));
    }

    @Test
    void countShouldReturnReportCount() throws Exception {
        when(adminService.countReports()).thenReturn(5L);

        mockMvc.perform(get("/admin/reports/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    void latestShouldReturnLatestReport() throws Exception {
        when(adminService.getLatestReport()).thenReturn(report(2L, "DAILY"));

        mockMvc.perform(get("/admin/reports/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void byTypeShouldReturnTypeFilteredReports() throws Exception {
        when(adminService.getReportsByType("DAILY")).thenReturn(List.of(report(1L, "DAILY")));

        mockMvc.perform(get("/admin/reports/type/DAILY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportType").value("DAILY"));
    }

    @Test
    void existsShouldReturnTrueWhenReportExists() throws Exception {
        when(adminService.reportExists(1L)).thenReturn(true);

        mockMvc.perform(get("/admin/reports/exist/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void byDateShouldReturnDateFilteredReports() throws Exception {
        when(adminService.getReportsByDate(LocalDate.of(2026, 3, 31))).thenReturn(List.of(report(1L, "DAILY")));

        mockMvc.perform(get("/admin/reports/by-date/2026-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void byRangeShouldReturnRangeFilteredReports() throws Exception {
        when(adminService.getReportsByRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31)))
                .thenReturn(List.of(report(1L, "DAILY")));

        mockMvc.perform(get("/admin/reports/by-range")
                        .param("from", "2026-03-01")
                        .param("to", "2026-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void byUserShouldReturnReportsByUser() throws Exception {
        when(adminService.getReportsByGeneratedBy("admin")).thenReturn(List.of(report(1L, "DAILY")));

        mockMvc.perform(get("/admin/reports/by-user/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].generatedBy").value("admin"));
    }

    @Test
    void reportTypeSummaryShouldReturnSummaryMap() throws Exception {
        when(adminService.reportTypeSummary()).thenReturn(Map.of("DAILY", 3L));

        mockMvc.perform(get("/admin/reports/summary/type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.DAILY").value(3));
    }

    @Test
    void dashboardShouldReturnAggregatedCounts() throws Exception {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setTotalDeliveries(5);
        dto.setShipped(2);
        dto.setDelivered(2);
        dto.setCancelled(1);
        when(adminService.getDashboard()).thenReturn(dto);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveries").value(5))
                .andExpect(jsonPath("$.delivered").value(2));
    }

    @Test
    void healthShouldReturnServiceMessage() throws Exception {
        mockMvc.perform(get("/admin/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Admin Service Running"));
    }

    private ReportDTO reportDto() {
        ReportDTO dto = new ReportDTO();
        dto.setReportType("DAILY");
        dto.setFromDate(LocalDate.of(2026, 3, 1));
        dto.setToDate(LocalDate.of(2026, 3, 31));
        dto.setGeneratedBy("admin");
        return dto;
    }

    private Report report(Long id, String type) {
        Report report = new Report();
        report.setId(id);
        report.setReportType(type);
        report.setReportDate(LocalDate.of(2026, 3, 31));
        report.setFromDate(LocalDate.of(2026, 3, 1));
        report.setToDate(LocalDate.of(2026, 3, 31));
        report.setReportData("sample");
        report.setGeneratedBy("admin");
        return report;
    }
}
