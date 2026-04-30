package com.agriserve.dto.response;

import com.agriserve.entity.ComplianceRecord;
import com.agriserve.entity.enums.ComplianceType;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for ComplianceRecord.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceRecordResponse {

    private Long complianceId;
    private Long entityId;
    private ComplianceType complianceType;
    private String result;
    private LocalDateTime checkDate;
    private String notes;
    private Status status;

    public static ComplianceRecordResponse from(ComplianceRecord record) {
        return ComplianceRecordResponse.builder()
                .complianceId(record.getComplianceId())
                .entityId(record.getEntityId())
                .complianceType(record.getComplianceType())
                .result(record.getResult())
                .checkDate(record.getCheckDate())
                .notes(record.getNotes())
                .build();
    }
}
