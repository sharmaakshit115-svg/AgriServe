package com.agriserve.dto.request;

import com.agriserve.entity.enums.ComplianceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for creating a compliance record.
 */
@Data
public class ComplianceRecordRequest {

    @NotNull(message = "Entity ID is required")
    private Long entityId;

    @NotNull(message = "Compliance type is required")
    private ComplianceType complianceType;

    @NotNull(message = "Result is required")
    private String result;

    private String notes;
}
