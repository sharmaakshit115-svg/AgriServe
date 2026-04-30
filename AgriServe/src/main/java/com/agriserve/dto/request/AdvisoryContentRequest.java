package com.agriserve.dto.request;

import com.agriserve.entity.enums.AdvisoryCategory;
import com.agriserve.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for creating or updating AdvisoryContent.
 */
@Data
public class AdvisoryContentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String fileUri;

    @NotNull(message = "Category is required")
    private AdvisoryCategory category;

    private Status status;
}
