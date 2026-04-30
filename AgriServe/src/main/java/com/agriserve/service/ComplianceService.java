package com.agriserve.service;

import com.agriserve.dto.request.AuditRequest;
import com.agriserve.dto.request.ComplianceRecordRequest;
import com.agriserve.dto.response.AuditResponse;
import com.agriserve.dto.response.ComplianceRecordResponse;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contract for Compliance & Audit management.
 */
public interface ComplianceService {

    // Compliance Records
    ComplianceRecordResponse createComplianceRecord(ComplianceRecordRequest request);

    ComplianceRecordResponse getComplianceRecordById(Long complianceId);

    Page<ComplianceRecordResponse> getComplianceByEntityId(Long entityId, Pageable pageable);

    Page<ComplianceRecordResponse> getAllComplianceRecords(Pageable pageable);

    // Audits
    AuditResponse createAudit(AuditRequest request);

    AuditResponse getAuditById(Long auditId);

    Page<AuditResponse> getAllAudits(Status status, Pageable pageable);

    AuditResponse updateAuditStatus(Long auditId, Status status);
}
