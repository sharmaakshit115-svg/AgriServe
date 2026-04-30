package com.agriserve.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Request DTO for submitting farmer feedback on an advisory session.
 * Rating is enforced to be between 1 and 5.
 */
@Data
public class FeedbackRequest {

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String comments;
}
