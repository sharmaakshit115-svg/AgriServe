package com.agriserve.repository;

import com.agriserve.entity.ComplianceRecord;
import com.agriserve.entity.enums.ComplianceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

    Page<ComplianceRecord> findAllByEntityId(Long entityId, Pageable pageable);

    Page<ComplianceRecord> findAllByComplianceType(ComplianceType type, Pageable pageable);
}
