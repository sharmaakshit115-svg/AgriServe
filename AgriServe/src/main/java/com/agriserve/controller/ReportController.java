package com.agriserve.controller;

import com.agriserve.dto.request.ReportRequest;
import com.agriserve.dto.response.ApiResponse;
import com.agriserve.dto.response.ReportResponse;
import com.agriserve.entity.enums.ReportScope;
import com.agriserve.service.ReportService;
import com.agriserve.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Reporting & Analytics.
 * Access restricted to Program Managers, Admins, and Government Auditors.
 */
@Tag(name = "Reports", description = "Analytics and report generation")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Generate a new report (Manager / Auditor / Admin)")
    @PostMapping
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER', 'ADMIN', 'GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<ReportResponse>> generateReport(
            @Valid @RequestBody ReportRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reportService.generateReport(request, userId), "Report generated"));
    }

    @Operation(summary = "Get report by ID")
    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER', 'ADMIN', 'GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<ReportResponse>> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getReportById(reportId)));
    }

    @Operation(summary = "Get all reports (filterable by scope)")
    @GetMapping
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER', 'ADMIN', 'GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> getAllReports(
            @RequestParam(required = false) ReportScope scope,
            @PageableDefault(size = 20, sort = "generatedDate") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAllReports(scope, pageable)));
    }

    @GetMapping("reports")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMIN','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>>getMyReports(Pageable pageable){
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(reportService.getReportByUserId(userId,pageable)));
    }
}
