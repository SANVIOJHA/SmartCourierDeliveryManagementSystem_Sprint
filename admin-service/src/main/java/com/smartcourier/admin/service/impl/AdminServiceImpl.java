package com.smartcourier.admin.service.impl;

import com.smartcourier.admin.client.DeliveryClient;
import com.smartcourier.admin.dto.AdminDashboardDTO;
import com.smartcourier.admin.dto.DeliverySummaryDTO;
import com.smartcourier.admin.dto.ReportDTO;
import com.smartcourier.admin.entity.Report;
import com.smartcourier.admin.exception.ResourceNotFoundException;
import com.smartcourier.admin.repository.ReportRepository;
import com.smartcourier.admin.service.AdminService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final DeliveryClient deliveryClient;
    private final ReportRepository repo;

    public AdminServiceImpl(DeliveryClient deliveryClient, ReportRepository repo) {
        this.deliveryClient = deliveryClient;
        this.repo = repo;
    }

    @Override
    public AdminDashboardDTO getDashboard() {
        List<DeliverySummaryDTO> deliveries;
        try {
            deliveries = deliveryClient.getAllDeliveries();
        } catch (Exception ex) {
            deliveries = List.of();
        }

        int total = deliveries != null ? deliveries.size() : 0;
        int shipped = deliveries != null ? (int) deliveries.stream().filter(d -> "SHIPPED".equalsIgnoreCase(d.getStatus())).count() : 0;
        int delivered = deliveries != null ? (int) deliveries.stream().filter(d -> "DELIVERED".equalsIgnoreCase(d.getStatus())).count() : 0;
        int cancelled = deliveries != null ? (int) deliveries.stream().filter(d -> "CANCELLED".equalsIgnoreCase(d.getStatus())).count() : 0;

        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setTotalDeliveries(total);
        dto.setShipped(shipped);
        dto.setDelivered(delivered);
        dto.setCancelled(cancelled);
        return dto;
    }

    @Override
    public Report generateReport(Report report) {
        report.setReportDate(LocalDate.now());
        if (report.getReportData() == null || report.getReportData().isBlank()) {
            report.setReportData("Auto-generated report payload");
        }
        return repo.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return repo.findAll();
    }

    @Override
    public Report getReport(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found for id: " + id));
    }

    @Override
    public Report updateReport(Long id, ReportDTO dto) {
        Report report = getReport(id);
        report.setReportType(dto.getReportType());
        report.setFromDate(dto.getFromDate());
        report.setToDate(dto.getToDate());
        report.setGeneratedBy(dto.getGeneratedBy());
        report.setReportDate(LocalDate.now());
        return repo.save(report);
    }

    @Override
    public void deleteReport(Long id) {
        Report report = getReport(id);
        repo.delete(report);
    }

    @Override
    public List<Report> getReportsByType(String reportType) {
        return repo.findByReportTypeIgnoreCase(reportType);
    }

    @Override
    public List<Report> getReportsByGeneratedBy(String generatedBy) {
        return repo.findByGeneratedByIgnoreCase(generatedBy);
    }

    @Override
    public List<Report> getReportsByDate(LocalDate reportDate) {
        return repo.findByReportDate(reportDate);
    }

    @Override
    public List<Report> getReportsByRange(LocalDate fromDate, LocalDate toDate) {
        return repo.findByReportDateBetween(fromDate, toDate);
    }

    @Override
    public long countReports() {
        return repo.count();
    }

    @Override
    public Report getLatestReport() {
        return repo.findAll().stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new ResourceNotFoundException("No reports available"));
    }

    @Override
    public boolean reportExists(Long id) {
        return repo.existsById(id);
    }

    @Override
    public Map<String, Long> reportTypeSummary() {
        Map<String, Long> summary = new HashMap<>();
        repo.findAll().forEach(report -> {
            String type = report.getReportType() == null ? "UNKNOWN" : report.getReportType().toUpperCase();
            summary.put(type, summary.getOrDefault(type, 0L) + 1);
        });
        return summary;
    }
}
