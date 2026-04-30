package com.agriserve.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for registering a farmer's participation in a workshop.
 *
 * <p><strong>Only EXTENSION_OFFICER</strong> may submit this request.
 * Attendance, feedback, and rating are NOT accepted here — they are set
 * through dedicated endpoints after the workshop is completed.</p>
 */
@Data
public class RegisterParticipationRequest {

    @NotNull(message = "Workshop ID is required")
    private Long workshopId;

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;
}
