package com.smartcourier.admin.service;

import com.smartcourier.admin.dto.AdminDashboardDTO;
import com.smartcourier.admin.dto.ReportDTO;
import com.smartcourier.admin.entity.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {

    Report generateReport(Report report);

    List<Report> getAllReports();

    Report getReport(Long id);

    Report updateReport(Long id, ReportDTO dto);

    void deleteReport(Long id);

    AdminDashboardDTO getDashboard();

    List<Report> getReportsByType(String reportType);

    List<Report> getReportsByGeneratedBy(String generatedBy);

    List<Report> getReportsByDate(LocalDate reportDate);

    List<Report> getReportsByRange(LocalDate fromDate, LocalDate toDate);

    long countReports();

    Report getLatestReport();

    boolean reportExists(Long id);

    Map<String, Long> reportTypeSummary();
}
