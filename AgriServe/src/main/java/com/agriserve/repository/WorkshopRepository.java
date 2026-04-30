package com.agriserve.repository;

import com.agriserve.entity.Workshop;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Long> {

    Page<Workshop> findAllByProgram_ProgramId(Long programId, Pageable pageable);

    Page<Workshop> findAllByStatus(Status status, Pageable pageable);

    Page<Workshop> findAllByOfficer_UserId(Long officerId, Pageable pageable);

    /**
     * Used by MetricSchedulerService to find workshops that completed more
     * than 24 hours ago and are eligible for metric computation.
     */
    @Query("SELECT w FROM Workshop w WHERE w.status = com.agriserve.entity.enums.Status.COMPLETED "
         + "AND w.completedAt IS NOT NULL AND w.completedAt < :cutoff")
    List<Workshop> findCompletedWorkshopsReadyForMetrics(@Param("cutoff") LocalDateTime cutoff);

    Object countByProgram_ProgramId(Long programId);

    List<Workshop> findByStatusAndMetricsCalculatedFalseAndCompletedAtBefore(Status status, LocalDateTime cutoff);
}
