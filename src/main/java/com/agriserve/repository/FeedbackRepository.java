package com.agriserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.agriserve.entity.Feedback;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	@Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.session.extensionOfficer.userId = :officerId")
	Double getAverageRatingByOfficer(@Param("officerId") Long officerId);

	List<Feedback> findBySession_ExtensionOfficer_UserId(Long officerId);
}
