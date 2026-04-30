package com.agriserve.repository;

import com.agriserve.entity.Participation;
import com.agriserve.entity.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Page<Participation> findAllByWorkshop_WorkshopId(Long workshopId, Pageable pageable);

    Page<Participation> findAllByFarmer_FarmerId(Long farmerId, Pageable pageable);

    Optional<Participation> findByWorkshop_WorkshopIdAndFarmer_FarmerId(Long workshopId, Long farmerId);

    @Query("SELECT COUNT(p) FROM Participation p WHERE p.workshop.workshopId = :workshopId AND p.attendanceStatus = :status")
    long countByWorkshopAndStatus(@Param("workshopId") Long workshopId, @Param("status") AttendanceStatus status);

    @Query("SELECT AVG(p.workshopRating) FROM Participation p " +
            "WHERE p.workshop.workshopId = :workshopId AND p.workshopRating IS NOT NULL")
    Double findAverageWorkshopRatingByWorkshopId(@Param("workshopId") Long workshopId);
    /**
     * Compute average workshopRating for all rated participations in a given program.
     * Used for PROGRAM_SATISFACTION metric.
     * NOTE: This is TRAINING data only — do NOT mix with Feedback.rating (advisory).
     */
    @Query("SELECT AVG(p.workshopRating) FROM Participation p "
         + "WHERE p.workshop.program.programId = :programId AND p.workshopRating IS NOT NULL")
    Double findAverageWorkshopRatingByProgramId(@Param("programId") Long programId);
}
