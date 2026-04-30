package com.agriserve.dto.response;

import com.agriserve.entity.Audit;
import com.agriserve.entity.enums.AuditScope;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Audit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditResponse {

    private Long auditId;
    private Long officerId;
    private String officerName;
    private AuditScope scope;
    private String findings;
    private LocalDateTime auditDate;
    private Status status;

    public static AuditResponse from(Audit audit) {
        return AuditResponse.builder()
                .auditId(audit.getAuditId())
                .officerId(audit.getOfficer() != null ? audit.getOfficer().getUserId() : null)
                .officerName(audit.getOfficer() != null ? audit.getOfficer().getName() : null)
                .scope(audit.getScope())
                .findings(audit.getFindings())
                .auditDate(audit.getAuditDate())
                .status(audit.getStatus())
                .build();
    }
}
