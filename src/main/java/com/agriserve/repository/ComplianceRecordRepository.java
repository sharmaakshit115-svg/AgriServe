package com.agriserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agriserve.entity.ComplianceRecord;
import java.util.List;


@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

	// Find all compliance reports for a specific Workshop or Advisory Session
	List<ComplianceRecord> findByEntityIdAndType(Long entityID, ComplianceRecord.ComplianceType type);

	// Find all records filtered by the Result (e.g., all "FAIL" records)
	List<ComplianceRecord> findByResult(ComplianceRecord.ComplianceResult result);

	// Find all records for a specific Extension Officer
	List<ComplianceRecord> findByExtensionOfficer_UserId(Long userId);
}
