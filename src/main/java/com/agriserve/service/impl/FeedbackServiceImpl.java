package com.agriserve.service.impl;

import com.agriserve.dto.request.FeedbackRequest;
import com.agriserve.dto.response.FeedbackResponse;
import com.agriserve.entity.AdvisorySession;
import com.agriserve.entity.Farmer;
import com.agriserve.entity.Feedback;
import com.agriserve.entity.SatisfactionMetric;
import com.agriserve.entity.User;
import com.agriserve.entity.enums.MetricType;
import com.agriserve.entity.enums.Status;
import com.agriserve.exception.BusinessException;
import com.agriserve.exception.ResourceNotFoundException;
import com.agriserve.repository.AdvisorySessionRepository;
import com.agriserve.repository.FarmerRepository;
import com.agriserve.repository.FeedbackRepository;
import com.agriserve.repository.SatisfactionMetricRepository;
import com.agriserve.repository.UserRepository;
import com.agriserve.service.AuditLogService;
import com.agriserve.service.FeedbackService;
import com.agriserve.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles advisory session feedback and officer performance metric computation.
 *
 * Data-flow: Advisory → Session → Feedback (Feedback.rating) → Officer Performance
 *
 * Fixes applied:
 *   Issue 3         – audit log on feedback submission
 *   Issue 6 / 9     – metric uses Feedback.rating for OFFICER_PERFORMANCE only;
 *                     PROGRAM_SATISFACTION metric is computed in MetricSchedulerService
 *                     from Participation.workshopRating (training data)
 *   Issue 7         – session must be COMPLETED before feedback is accepted
 *   Session Ownership – feedback submitter must be the session's own farmer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FarmerRepository farmerRepository;
    private final AdvisorySessionRepository sessionRepository;
    private final SatisfactionMetricRepository metricRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public FeedbackResponse submitFeedback(FeedbackRequest request) {
        Farmer farmer = farmerRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", request.getFarmerId()));
        AdvisorySession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("AdvisorySession", "id", request.getSessionId()));

        // ── Issue 7: Session must be COMPLETED before submitting feedback ──
        if (session.getStatus() != Status.COMPLETED) {
            throw new BusinessException(
                "Feedback can only be submitted for COMPLETED sessions. "
                + "Current status: " + session.getStatus());
        }

        // ── Session Ownership Fix: Only the session's own farmer can submit ─
        if (!session.getFarmer().getFarmerId().equals(request.getFarmerId())) {
            throw new BusinessException(
                "You cannot give feedback for another farmer's session.");
        }

        // ── Enforce one feedback per farmer per session ────────────────────
        feedbackRepository.findByFarmer_FarmerIdAndSession_SessionId(
                request.getFarmerId(), request.getSessionId())
                .ifPresent(f -> {
                    throw new BusinessException("Feedback already submitted for this session by this farmer.");
                });

        Feedback feedback = Feedback.builder()
                .farmer(farmer)
                .session(session)
                .rating(request.getRating())
                .comments(request.getComments())
                .build();
        Feedback saved = feedbackRepository.save(feedback);

        // ── Issue 3: Audit log ─────────────────────────────────────────────
        auditLogService.log(farmer.getUser(), "SUBMIT_FEEDBACK",
                "Feedback#" + saved.getFeedbackId(),
                "Session#" + session.getSessionId() + " rating=" + request.getRating());

        log.info("Feedback submitted: id={}, session={}, farmer={}, rating={}",
                saved.getFeedbackId(), session.getSessionId(), farmer.getFarmerId(), request.getRating());
        return FeedbackResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(Long feedbackId) {
        return FeedbackResponse.from(feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", feedbackId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackResponse> getFeedbackByFarmer(Long farmerId, Pageable pageable) {
        return feedbackRepository.findAllByFarmer_FarmerId(farmerId, pageable).map(FeedbackResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackResponse> getFeedbackBySession(Long sessionId, Pageable pageable) {
        return feedbackRepository.findAllBySession_SessionId(sessionId, pageable).map(FeedbackResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingBySession(Long sessionId) {
        Double avg = feedbackRepository.findAverageRatingBySessionId(sessionId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }


    @Override
    @Transactional
    public Double computeAndStoreSatisfactionMetric(Long officerId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("User (Officer)", "id", officerId));

        Double avg = feedbackRepository.findAverageRatingByOfficerId(officerId);
        double score = avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;

        SatisfactionMetric metric = SatisfactionMetric.builder()
                .officer(officer)
                .metricType(MetricType.OFFICER_PERFORMANCE)
                .score(score)
                .status(Status.ACTIVE)
                .build();
        metricRepository.save(metric);

        log.info("Officer performance metric computed for officer {}: {}", officerId, score);
        auditLogService.log(officer,"COMPUTE_OFFICER_PERFORMANCE","Officer#" + officerId, "score="+score);
        return score;
    }
}
