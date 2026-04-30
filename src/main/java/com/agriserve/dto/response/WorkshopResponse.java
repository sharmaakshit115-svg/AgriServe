package com.agriserve.dto.response;

import com.agriserve.entity.Workshop;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Workshop.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopResponse {

    private Long workshopId;
    private Long programId;
    private String programTitle;
    private Long officerId;
    private String officerName;
    private String location;
    private LocalDateTime workshopDate;
    private LocalDateTime completedAt;
    private Status status;
    private LocalDateTime createdAt;

    public static WorkshopResponse from(Workshop workshop) {
        return WorkshopResponse.builder()
                .workshopId(workshop.getWorkshopId())
                .programId(workshop.getProgram() != null ? workshop.getProgram().getProgramId() : null)
                .programTitle(workshop.getProgram() != null ? workshop.getProgram().getTitle() : null)
                .officerId(workshop.getOfficer() != null ? workshop.getOfficer().getUserId() : null)
                .officerName(workshop.getOfficer() != null ? workshop.getOfficer().getName() : null)
                .location(workshop.getLocation())
                .workshopDate(workshop.getWorkshopDate())
                .completedAt(workshop.getCompletedAt())
                .status(workshop.getStatus())
                .createdAt(workshop.getCreatedAt())
                .build();
    }
}
