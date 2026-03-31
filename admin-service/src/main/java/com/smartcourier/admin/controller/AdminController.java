package com.smartcourier.admin.controller;

import com.smartcourier.admin.dto.AdminDashboardDTO;
import com.smartcourier.admin.dto.ReportDTO;
import com.smartcourier.admin.entity.Report;
import com.smartcourier.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @PostMapping("/reports")
    public Report create(@Valid @RequestBody ReportDTO dto) {
        Report report = new Report();
        report.setReportType(dto.getReportType());
        report.setFromDate(dto.getFromDate());
        report.setToDate(dto.getToDate());
        report.setGeneratedBy(dto.getGeneratedBy());
        return service.generateReport(report);
    }

    @GetMapping("/reports")
    public List<Report> getAll() {
        return service.getAllReports();
    }

    @GetMapping("/reports/{id}")
    public Report get(@PathVariable Long id) {
        return service.getReport(id);
    }

    @PutMapping("/reports/{id}")
    public Report update(@PathVariable Long id, @Valid @RequestBody ReportDTO dto) {
        return service.updateReport(id, dto);
    }

    @DeleteMapping("/reports/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteReport(id);
        return "Deleted";
    }

    @GetMapping("/reports/count")
    public long count() {
        return service.countReports();
    }

    @GetMapping("/reports/latest")
    public Report latest() {
        return service.getLatestReport();
    }

    @GetMapping("/reports/type/{type}")
    public List<Report> byType(@PathVariable String type) {
        return service.getReportsByType(type);
    }

    @GetMapping("/reports/exist/{id}")
    public boolean exists(@PathVariable Long id) {
        return service.reportExists(id);
    }

    @GetMapping("/reports/by-date/{date}")
    public List<Report> byDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getReportsByDate(date);
    }

    @GetMapping("/reports/by-range")
    public List<Report> byRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getReportsByRange(from, to);
    }

    @GetMapping("/reports/by-user/{username}")
    public List<Report> byUser(@PathVariable String username) {
        return service.getReportsByGeneratedBy(username);
    }

    @GetMapping("/reports/summary/type")
    public Map<String, Long> reportTypeSummary() {
        return service.reportTypeSummary();
    }

    @GetMapping("/dashboard")
    public AdminDashboardDTO dashboard() {
        return service.getDashboard();
    }

    @GetMapping("/health")
    public String health() {
        return "Admin Service Running";
    }
}
