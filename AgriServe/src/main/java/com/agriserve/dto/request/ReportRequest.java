package com.agriserve.dto.request;

import com.agriserve.entity.enums.ReportScope;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for generating a report.
 */
@Data
public class ReportRequest {

    @NotNull(message = "Scope is required")
    private ReportScope scope;

    private String title;

    /** Optional filter: specific program ID if scope = PROGRAM_LEVEL */
    private Long programId;
}
