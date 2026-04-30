package com.agriserve.dto.request;

import com.agriserve.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating a Training Program.
 */
@Data
public class TrainingProgramRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Status status;
}
