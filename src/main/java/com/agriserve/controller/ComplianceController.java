package com.agriserve.controller;

import com.agriserve.dto.request.AuditRequest;
import com.agriserve.dto.request.ComplianceRecordRequest;
import com.agriserve.dto.response.ApiResponse;
import com.agriserve.dto.response.AuditResponse;
import com.agriserve.dto.response.ComplianceRecordResponse;
import com.agriserve.entity.enums.Status;
import com.agriserve.service.ComplianceService;
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
 * REST controller for Compliance & Audit Management.
 */
@Tag(name = "Compliance", description = "Compliance records and formal audits")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;

    // ─── Compliance Records ───────────────────────────────────────────────────

    @Operation(summary = "Create a compliance record (Compliance Officer / Admin)")
    @PostMapping("/records")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> createRecord(
            @Valid @RequestBody ComplianceRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(complianceService.createComplianceRecord(request), "Compliance record created"));
    }

    @Operation(summary = "Get compliance record by ID")
    @GetMapping("/records/{complianceId}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'GOVERNMENT_AUDITOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> getRecord(@PathVariable Long complianceId) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getComplianceRecordById(complianceId)));
    }

    @Operation(summary = "Get all compliance records for a specific entity (farmer / program / workshop)")
    @GetMapping("/records/entity/{entityId}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'GOVERNMENT_AUDITOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ComplianceRecordResponse>>> getByEntity(
            @PathVariable Long entityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getComplianceByEntityId(entityId, pageable)));
    }

    @Operation(summary = "Get all compliance records")
    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'GOVERNMENT_AUDITOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ComplianceRecordResponse>>> getAllRecords(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getAllComplianceRecords(pageable)));
    }

    // ─── Audits ───────────────────────────────────────────────────────────────

    @Operation(summary = "Create a formal audit (Compliance Officer / Auditor)")
    @PostMapping("/audits")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'GOVERNMENT_AUDITOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AuditResponse>> createAudit(
            @Valid @RequestBody AuditRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(complianceService.createAudit(request), "Audit initiated"));
    }

    @Operation(summary = "Get audit by ID")
    @GetMapping("/audits/{auditId}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'GOVERNMENT_AUDITOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AuditResponse>> getAudit(@PathVariable Long auditId) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getAuditById(auditId)));
    }

    @Operation(summary = "Get all audits (filterable by status)")
    @GetMapping("/audits")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'GOVERNMENT_AUDITOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditResponse>>> getAllAudits(
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getAllAudits(status, pageable)));
    }

    @Operation(summary = "Update audit status (Compliance Officer / Admin)")
    @PatchMapping("/audits/{auditId}/status")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'GOVERNMENT_AUDITOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AuditResponse>> updateAuditStatus(
            @PathVariable Long auditId,
            @RequestParam Status status) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.updateAuditStatus(auditId, status)));
    }
}
