package com.smartcourier.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType;   // DAILY, MONTHLY

    private LocalDate reportDate;

    private LocalDate fromDate;
    private LocalDate toDate;

    @Column(length = 2000)
    private String reportData;

    private String generatedBy;
}