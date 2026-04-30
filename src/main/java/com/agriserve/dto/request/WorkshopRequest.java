package com.agriserve.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for scheduling a Workshop under a Training Program.
 */
@Data
public class WorkshopRequest {

    @NotNull(message = "Program ID is required")
    private Long programId;

    @NotNull(message = "Officer ID is required")
    private Long officerId;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Workshop date is required")
    private LocalDateTime workshopDate;
}
