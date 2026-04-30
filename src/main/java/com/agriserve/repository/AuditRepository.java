package com.agriserve.repository;

import com.agriserve.entity.Audit;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    Page<Audit> findAllByOfficer_UserId(Long officerId, Pageable pageable);

    Page<Audit> findAllByStatus(Status status, Pageable pageable);
}
