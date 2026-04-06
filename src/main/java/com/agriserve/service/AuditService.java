package com.agriserve.service;

import com.agriserve.dto.AuditDTO;
import com.agriserve.entity.Audit;
import com.agriserve.entity.User;
import com.agriserve.repository.AuditRepository;
import com.agriserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepo;
    private final UserRepository userRepo;

    public Audit createAudit(AuditDTO dto) {
        User officer = userRepo.findById(dto.getOfficerId())
                .orElseThrow(() -> new RuntimeException("Officer not found for Audit"));

        Audit audit = new Audit();
        audit.setOfficer(officer);
        audit.setScope(dto.getScope());
        audit.setFindings(dto.getFindings());
        audit.setStatus(dto.getStatus());

        return auditRepo.save(audit);
    }

    public List<Audit> getAuditsForOfficer(Long officerId) {
        return auditRepo.findByOfficer_UserId(officerId);
    }

    public List<Audit> getAuditsByStatus(String statusString) {

            Audit.AuditStatus status = Audit.AuditStatus.valueOf(statusString.toUpperCase());
            return auditRepo.findByStatus(status);
    }
    public Audit updateAuditStatus(Long auditId, Audit.AuditStatus newStatus) {
        Audit audit = auditRepo.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit record not found"));

        audit.setStatus(newStatus);
        return auditRepo.save(audit);
    }
}
