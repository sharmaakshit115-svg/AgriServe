package com.agriserve.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Request DTO for a farmer submitting workshop feedback and/or a satisfaction rating.
 *
 * <p><strong>Only the FARMER who attended (PRESENT) the workshop</strong> may submit
 * this request, and only after the workshop is COMPLETED.
 * Both fields are optional individually — the farmer may submit just a rating,
 * just feedback, or both in a single call.</p>
 *
 * <p>This replaces the separate rating and feedback endpoints, enforcing
 * the single-step rule: one API, one lifecycle stage.</p>
 */
@Data
public class SubmitFeedbackAndRatingRequest {

    /** Free-text feedback from the attending farmer. Optional. */
    private String feedback;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
}
