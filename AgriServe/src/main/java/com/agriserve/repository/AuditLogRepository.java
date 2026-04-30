package com.agriserve.repository;

import com.agriserve.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByUser_UserId(Long userId, Pageable pageable);

    Page<AuditLog> findAllByAction(String action, Pageable pageable);
}
