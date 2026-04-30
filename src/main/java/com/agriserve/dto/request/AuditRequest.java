package com.agriserve.dto.request;

import com.agriserve.entity.enums.AuditScope;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for creating a formal audit record.
 */
@Data
public class AuditRequest {

    @NotNull(message = "Officer ID is required")
    private Long officerId;

    @NotNull(message = "Scope is required")
    private AuditScope scope;

    private String findings;
}
