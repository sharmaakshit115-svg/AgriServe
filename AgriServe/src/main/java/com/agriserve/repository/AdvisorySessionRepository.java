package com.agriserve.repository;

import com.agriserve.entity.AdvisorySession;
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
public interface AdvisorySessionRepository extends JpaRepository<AdvisorySession, Long> {

    Page<AdvisorySession> findAllByFarmer_FarmerId(Long farmerId, Pageable pageable);

    Page<AdvisorySession> findAllByOfficer_UserId(Long officerId, Pageable pageable);

    Page<AdvisorySession> findAllByStatus(Status status, Pageable pageable);

    /**
     * Officer double-booking guard: returns true if the officer already has a
     * PENDING or APPROVED session with this farmer for sessions near the same date.
     */
    boolean existsByOfficer_UserIdAndFarmer_FarmerIdAndStatus(
            Long officerId, Long farmerId, Status status);
}
