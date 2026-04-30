package com.agriserve.dto.response;

import com.agriserve.entity.AdvisorySession;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for AdvisorySession.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvisorySessionResponse {

    private Long sessionId;
    private Long officerId;
    private String officerName;
    private Long farmerId;
    private String farmerName;
    private Long contentId;
    private String contentTitle;
    private LocalDateTime sessionDate;
    private String feedback;
    private Status status;
    private LocalDateTime createdAt;

    public static AdvisorySessionResponse from(AdvisorySession session) {
        return AdvisorySessionResponse.builder()
                .sessionId(session.getSessionId())
                .officerId(session.getOfficer() != null ? session.getOfficer().getUserId() : null)
                .officerName(session.getOfficer() != null ? session.getOfficer().getName() : null)
                .farmerId(session.getFarmer() != null ? session.getFarmer().getFarmerId() : null)
                .farmerName(session.getFarmer() != null ? session.getFarmer().getName() : null)
                .contentId(session.getContent() != null ? session.getContent().getContentId() : null)
                .contentTitle(session.getContent() != null ? session.getContent().getTitle() : null)
                .sessionDate(session.getSessionDate())
                .feedback(session.getFeedback())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
