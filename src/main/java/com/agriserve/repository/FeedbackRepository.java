package com.agriserve.repository;

import com.agriserve.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Page<Feedback> findAllByFarmer_FarmerId(Long farmerId, Pageable pageable);

    Page<Feedback> findAllBySession_SessionId(Long sessionId, Pageable pageable);

    Optional<Feedback> findByFarmer_FarmerIdAndSession_SessionId(Long farmerId, Long sessionId);

    /** Compute average advisory rating for a specific session — feeds officer performance */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.session.sessionId = :sessionId")
    Double findAverageRatingBySessionId(@Param("sessionId") Long sessionId);

    /**
     * Compute average Feedback rating for all advisory sessions handled by a specific officer.
     * Used for OFFICER_PERFORMANCE metric.
     * NOTE: This is ADVISORY data only — do NOT mix with Participation.workshopRating.
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.session.officer.userId = :officerId")
    Double findAverageRatingByOfficerId(@Param("officerId") Long officerId);

}
