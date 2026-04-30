package com.agriserve.dto.response;

import com.agriserve.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Feedback.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private Long feedbackId;
    private Long farmerId;
    private String farmerName;
    private Long sessionId;
    private Integer rating;
    private String comments;
    private LocalDateTime feedbackDate;

    public static FeedbackResponse from(Feedback feedback) {
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .farmerId(feedback.getFarmer() != null ? feedback.getFarmer().getFarmerId() : null)
                .farmerName(feedback.getFarmer() != null ? feedback.getFarmer().getName() : null)
                .sessionId(feedback.getSession() != null ? feedback.getSession().getSessionId() : null)
                .rating(feedback.getRating())
                .comments(feedback.getComments())
                .feedbackDate(feedback.getFeedbackDate())
                .build();
    }
}
