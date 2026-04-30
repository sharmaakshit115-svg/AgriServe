package com.agriserve.service.impl;

import com.agriserve.dto.request.AuditRequest;
import com.agriserve.dto.request.ComplianceRecordRequest;
import com.agriserve.dto.response.AuditResponse;
import com.agriserve.dto.response.ComplianceRecordResponse;
import com.agriserve.entity.Audit;
import com.agriserve.entity.ComplianceRecord;
import com.agriserve.entity.User;
import com.agriserve.entity.enums.ComplianceType;
import com.agriserve.entity.enums.Status;
import com.agriserve.exception.BusinessException;
import com.agriserve.exception.ResourceNotFoundException;
import com.agriserve.repository.*;
import com.agriserve.service.AuditLogService;
import com.agriserve.service.ComplianceService;
import com.agriserve.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.agriserve.entity.enums.ComplianceType.*;

/**
 * Manages compliance records and formal audits.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceRecordRepository complianceRecordRepository;
    private final AuditRepository auditRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final WorkshopRepository workshopRepository;
    private final AdvisorySessionRepository advisorySessionRepository;

    // ─── Compliance Records ───────────────────────────────────────────────────

    @Override
    @Transactional
    public ComplianceRecordResponse createComplianceRecord(ComplianceRecordRequest request) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(()->new ResourceNotFoundException("User","id",currentUserId));

       validateEntity(request.getEntityId(),request.getComplianceType());

        ComplianceRecord record = ComplianceRecord.builder()
                .entityId(request.getEntityId())
                .complianceType(request.getComplianceType())
                .result(request.getResult())
                .notes(request.getNotes())
                .build();

        ComplianceRecord saved = complianceRecordRepository.save(record);

        log.info("Compliance Record is created with id={}, type={}",saved.getComplianceId(),saved.getComplianceType());
        auditLogService.log(currentUserId,"CREATE_COMPLIANCE_RECORD","Compliance#"+saved.getComplianceId()+", entity="+ request.getEntityId());
        return ComplianceRecordResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplianceRecordResponse getComplianceRecordById(Long complianceId) {
        return ComplianceRecordResponse.from(
                complianceRecordRepository.findById(complianceId)
                        .orElseThrow(() -> new ResourceNotFoundException("ComplianceRecord", "id", complianceId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplianceRecordResponse> getComplianceByEntityId(Long entityId, Pageable pageable) {
        return complianceRecordRepository.findAllByEntityId(entityId, pageable)
                .map(ComplianceRecordResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplianceRecordResponse> getAllComplianceRecords(Pageable pageable) {
        return complianceRecordRepository.findAll(pageable).map(ComplianceRecordResponse::from);
    }

    // ─── Audits ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuditResponse createAudit(AuditRequest request) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOfficerId()));

        Audit audit = Audit.builder()
                .officer(officer)
                .scope(request.getScope())
                .findings(request.getFindings())
                .status(Status.PENDING)
                .build();

        Audit saved = auditRepository.save(audit);

        log.info("Audit Record is created with id={}, officerId={}",saved.getAuditId(),saved.getOfficer().getUserId());
        auditLogService.log(currentUserId,"CREATE_AUDIT_RECORD","Audit#"+saved.getAuditId()+ ", officerId="+request.getOfficerId());
        return AuditResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditResponse getAuditById(Long auditId) {
        return AuditResponse.from(
                auditRepository.findById(auditId)
                        .orElseThrow(() -> new ResourceNotFoundException("Audit", "id", auditId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponse> getAllAudits(Status status, Pageable pageable) {
        if (status != null) {
            return auditRepository.findAllByStatus(status, pageable).map(AuditResponse::from);
        }
        return auditRepository.findAll(pageable).map(AuditResponse::from);
    }

    @Override
    @Transactional
    public AuditResponse updateAuditStatus(Long auditId, Status status) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(()->new ResourceNotFoundException("User","id",currentUserId));

        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new ResourceNotFoundException("Audit", "id", auditId));

        audit.setStatus(status);
        Audit updated = auditRepository.save(audit);

        log.info("Audit status is updated id={}",updated.getAuditId());
        auditLogService.log(currentUserId,"AUDIT_STATUS_UPDATED","Audit#"+updated.getAuditId()+", status="+status);
        return AuditResponse.from(audit);
    }

    // Helper

    private void validateEntity(Long entityId, ComplianceType type) {

        switch (type) {

            case TRAINING -> {
                if (!workshopRepository.existsById(entityId)) {
                    throw new ResourceNotFoundException("Workshop", "id", entityId);
                }
            }

            case ADVISORY -> {
                if (!advisorySessionRepository.existsById(entityId)) {
                    throw new ResourceNotFoundException("AdvisorySession", "id", entityId);
                }
            }

            default -> throw new BusinessException("Invalid compliance type");
        }
    }
}
