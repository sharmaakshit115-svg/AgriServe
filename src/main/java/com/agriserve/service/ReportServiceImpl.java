package com.agriserve.service;

import com.agriserve.entity.Report;
import com.agriserve.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository repo;

    @Override
    public Report generateReport(String scope) {

        Report report = new Report();
        report.setScope(scope);
        report.setGeneratedDate(LocalDateTime.now());

        // Placeholder metrics (can be JSON)
        report.setMetrics("{\"generated\":\"true\",\"scope\":\"" + scope + "\"}");

        return repo.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return repo.findAll();
    }
}