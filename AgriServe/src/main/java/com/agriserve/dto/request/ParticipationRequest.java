package com.agriserve.dto.request;

import com.agriserve.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for registering or updating farmer participation in a workshop.
 */
@Data
public class ParticipationRequest {

    @NotNull(message = "Workshop ID is required")
    private Long workshopId;

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus attendanceStatus;

    private String feedback;

    /**
     * Farmer's satisfaction rating for the workshop (1–5).
     * Used to compute PROGRAM_SATISFACTION metric.
     * Optional at registration; can be submitted separately after attendance.
     */
    @Min(value = 1, message = "Workshop rating must be at least 1")
    @Max(value = 5, message = "Workshop rating must be at most 5")
    private Integer workshopRating;
}
