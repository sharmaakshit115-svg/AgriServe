package com.agriserve.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for booking/scheduling an advisory session.
 */
@Data
public class AdvisorySessionRequest {

    @NotNull(message = "Officer ID is required")
    private Long officerId;

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;

    private Long contentId;

    @NotNull(message = "Session date is required")
    private LocalDateTime sessionDate;
}
