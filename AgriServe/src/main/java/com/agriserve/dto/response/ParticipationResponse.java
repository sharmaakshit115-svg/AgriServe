package com.agriserve.dto.response;

import com.agriserve.entity.Participation;
import com.agriserve.entity.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Participation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationResponse {

    private Long participationId;
    private Long workshopId;
    private String workshopLocation;
    private Long farmerId;
    private String farmerName;
    private AttendanceStatus attendanceStatus;
    private String feedback;
    private Integer workshopRating;   // training satisfaction score (1-5)
    private LocalDateTime createdAt;

    public static ParticipationResponse from(Participation p) {
        return ParticipationResponse.builder()
                .participationId(p.getParticipationId())
                .workshopId(p.getWorkshop() != null ? p.getWorkshop().getWorkshopId() : null)
                .workshopLocation(p.getWorkshop() != null ? p.getWorkshop().getLocation() : null)
                .farmerId(p.getFarmer() != null ? p.getFarmer().getFarmerId() : null)
                .farmerName(p.getFarmer() != null ? p.getFarmer().getName() : null)
                .attendanceStatus(p.getAttendanceStatus())
                .feedback(p.getFeedback())
                .workshopRating(p.getWorkshopRating())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
