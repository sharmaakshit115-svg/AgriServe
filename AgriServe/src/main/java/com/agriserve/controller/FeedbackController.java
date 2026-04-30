package com.agriserve.controller;

import com.agriserve.dto.request.FeedbackRequest;
import com.agriserve.dto.response.ApiResponse;
import com.agriserve.dto.response.FeedbackResponse;
import com.agriserve.entity.SatisfactionMetric;
import com.agriserve.repository.SatisfactionMetricRepository;
import com.agriserve.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Feedback & Satisfaction Monitoring.
 */
@Tag(name = "Feedback", description = "Farmer feedback submission and satisfaction metrics")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final SatisfactionMetricRepository metricRepository;

    @Operation(summary = "Submit feedback for an advisory session (FARMER only)")
    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(feedbackService.submitFeedback(request), "Feedback submitted"));
    }

    @Operation(summary = "Get feedback by ID (Officer / Manager / Admin)")
    @GetMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'PROGRAM_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedbackById(@PathVariable Long feedbackId) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getFeedbackById(feedbackId)));
    }

    @Operation(summary = "Get all feedback submitted by a farmer (own feedback only for FARMER role)")
    @GetMapping("/farmer/{farmerId}")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'PROGRAM_MANAGER', 'ADMIN') or @securityService.isOwner(#farmerId)")
    public ResponseEntity<ApiResponse<Page<FeedbackResponse>>> getFeedbackByFarmer(
            @PathVariable Long farmerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getFeedbackByFarmer(farmerId, pageable)));
    }

    @Operation(summary = "Get all feedback for an advisory session (Officer / Manager / Admin)")
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'PROGRAM_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<FeedbackResponse>>> getFeedbackBySession(
            @PathVariable Long sessionId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getFeedbackBySession(sessionId, pageable)));
    }

    @Operation(summary = "Get average advisory rating for a session (Officer / Manager / Admin)")
    @GetMapping("/session/{sessionId}/average-rating")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'PROGRAM_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getAverageRatingBySession(sessionId)));
    }

    @Operation(summary = "Compute and persist OFFICER PERFORMANCE metric (Manager / Admin)")
    @PostMapping("/satisfaction/officer/{officerId}/compute")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Double>> computeSatisfactionMetric(@PathVariable Long officerId) {
        Double score = feedbackService.computeAndStoreSatisfactionMetric(officerId);
        return ResponseEntity.ok(ApiResponse.success(score, "Officer performance metric computed and stored"));
    }


    @GetMapping("/program/{programId}")
    @PreAuthorize("hasAnyRole('ADMIN',PROGRAM_MANAGER)")
    public ResponseEntity<ApiResponse<List<SatisfactionMetric>>>getMetricsByProgram(@PathVariable Long programId){
        return ResponseEntity.ok(ApiResponse.success(metricRepository.findAllByProgram_ProgramId(programId)));
    }
}
