package com.agriserve.service;

import com.agriserve.entity.Report;
import java.util.List;

public interface ReportService {

    Report generateReport(String scope);

    List<Report> getAllReports();
}