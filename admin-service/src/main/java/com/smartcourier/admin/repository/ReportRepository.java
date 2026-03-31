package com.smartcourier.admin.repository;

import com.smartcourier.admin.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReportTypeIgnoreCase(String reportType);

    List<Report> findByGeneratedByIgnoreCase(String generatedBy);

    List<Report> findByReportDate(LocalDate reportDate);

    List<Report> findByReportDateBetween(LocalDate fromDate, LocalDate toDate);
}
