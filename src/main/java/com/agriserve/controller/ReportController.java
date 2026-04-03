package com.agriserve.controller;

import com.agriserve.entity.Report;
import com.agriserve.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService service;

    @PostMapping("/generate")
    public Report generateReport(@RequestParam String scope) {
        return service.generateReport(scope);
    }

    @GetMapping
    public List<Report> getReports() {
        return service.getAllReports();
    }
}
