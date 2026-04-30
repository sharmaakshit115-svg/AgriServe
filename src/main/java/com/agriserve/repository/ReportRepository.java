package com.agriserve.repository;

import com.agriserve.entity.Report;
import com.agriserve.entity.enums.ReportScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findAllByScope(ReportScope scope, Pageable pageable);

    Page<Report> findAllByGeneratedBy_UserId(Long userId, Pageable pageable);
}
