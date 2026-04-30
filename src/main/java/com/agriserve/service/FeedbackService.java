package com.agriserve.service;

import com.agriserve.dto.request.FeedbackRequest;
import com.agriserve.dto.response.FeedbackResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contract for Feedback & Satisfaction Monitoring.
 */
public interface FeedbackService {

    FeedbackResponse submitFeedback(FeedbackRequest request);

    FeedbackResponse getFeedbackById(Long feedbackId);

    Page<FeedbackResponse> getFeedbackByFarmer(Long farmerId, Pageable pageable);

    Page<FeedbackResponse> getFeedbackBySession(Long sessionId, Pageable pageable);

    Double getAverageRatingBySession(Long sessionId);

    /**
     * Computes and stores OFFICER_PERFORMANCE metric using AVG(Feedback.rating)
     * for all advisory sessions handled by the officer.
     * @param officerId the User ID of the Extension Officer
     */
    Double computeAndStoreSatisfactionMetric(Long officerId);
}
