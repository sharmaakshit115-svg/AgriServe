package com.agriserve.service;

import com.agriserve.dto.ComplianceDTO;
import com.agriserve.entity.ComplianceRecord;
import com.agriserve.entity.User;
import com.agriserve.repository.AdvisorySessionRepository;
import com.agriserve.repository.ComplianceRecordRepository;
import com.agriserve.repository.UserRepository;
import com.agriserve.repository.WorkshopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplianceService {

    private final ComplianceRecordRepository complianceRepo;
    private final UserRepository userRepo;
    // We inject these to verify the EntityID actually exists
    private final WorkshopRepository workshopRepo;
    private final AdvisorySessionRepository sessionRepo;

    public ComplianceRecord saveCompliance(ComplianceDTO dto) {
        // 1. Verify the Extension Officer exists
        User officer = userRepo.findById(dto.getOfficerId())
                .orElseThrow(() -> new RuntimeException("Extension Officer not found with ID: " + dto.getOfficerId()));

        // 2. Business Logic: Verify the Workshop or Session exists based on Type
        if (dto.getType() == ComplianceRecord.ComplianceType.TRAINING) {
            if (!workshopRepo.existsById(dto.getEntityId())) {
                throw new RuntimeException("Workshop not found for Compliance ID: " + dto.getEntityId());
            }
        } else if (dto.getType() == ComplianceRecord.ComplianceType.ADVISORY) {
            if (!sessionRepo.existsById(dto.getEntityId())) {
                throw new RuntimeException("Advisory Session not found for Compliance ID: " + dto.getEntityId());
            }
        }

        // 3. Map DTO to Entity
        ComplianceRecord record = new ComplianceRecord();
        record.setEntityId(dto.getEntityId());
        record.setType(dto.getType());
        record.setResult(dto.getResult());
        record.setNotes(dto.getNotes());
        record.setExtensionOfficer(officer);

        return complianceRepo.save(record);
    }

    public List<ComplianceRecord> getRecordsByEntity(Long entityId, ComplianceRecord.ComplianceType type) {
       return complianceRepo.findByEntityIdAndType(entityId, type);
    }

    public List<ComplianceRecord> getRecordsByResult(ComplianceRecord.ComplianceResult result) {
        return complianceRepo.findByResult(result);
    }

    public List<ComplianceRecord> getRecordsByOfficer(Long userId) {
        return complianceRepo.findByExtensionOfficer_UserId(userId);
    }
}
