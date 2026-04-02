package com.agriserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.agriserve.entity.Audit;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    // Find all audits performed on a specific Extension Officer
    List<Audit> findByOfficer_UserId(Long userId);

    // Find audits based on their status (OPEN, IN_PROGRESS, RESOLVED)
    List<Audit> findByStatus(Audit.AuditStatus status);

    // Find audits within a specific Scope string
    List<Audit> findByScopeContaining(String locationOrProgram);
}