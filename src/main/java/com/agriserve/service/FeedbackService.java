package com.agriserve.service;

import com.agriserve.dto.FeedbackDTO;
import com.agriserve.entity.*;
import com.agriserve.repository.AdvisorySessionRepository;
import com.agriserve.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.agriserve.repository.FarmerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepo;
    private final AdvisorySessionRepository sessionRepo;
    private final FarmerRepository farmerRepo;

    public Feedback submitFeedback(FeedbackDTO dto) {

        AdvisorySession session = sessionRepo.findById(dto.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found with ID: " + dto.getSessionId()));


        Farmer farmer = farmerRepo.findById(dto.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + dto.getFarmerId()));

        boolean isFarmerInSession = sessionRepo.isFarmerInSession(dto.getSessionId(),dto.getFarmerId());

        if (!isFarmerInSession) {
            throw new RuntimeException("Unauthorized: Farmer " + dto.getFarmerId() +
                    " cannot give feedback for Session " + dto.getSessionId() +
                    " because they were not a participant.");
        }

        Feedback feedback = new Feedback();
        feedback.setSession(session);
        feedback.setFarmer(farmer);
        feedback.setRating(dto.getRating());
        feedback.setComments(dto.getComments());

        return feedbackRepo.save(feedback);
    }
    public Double getOfficerPerformance(Long officerId) {
        return feedbackRepo.getAverageRatingByOfficer(officerId);
    }

    public List<Feedback> getFeedbackForOfficer(Long officerId) {
        return feedbackRepo.findBySession_ExtensionOfficer_UserId(officerId);
    }
}