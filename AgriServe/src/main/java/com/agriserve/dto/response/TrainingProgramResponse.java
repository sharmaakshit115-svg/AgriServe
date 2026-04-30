package com.agriserve.dto.response;

import com.agriserve.entity.TrainingProgram;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for TrainingProgram.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgramResponse {

    private Long programId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private LocalDateTime createdAt;

    public static TrainingProgramResponse from(TrainingProgram program) {
        return TrainingProgramResponse.builder()
                .programId(program.getProgramId())
                .title(program.getTitle())
                .description(program.getDescription())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .status(program.getStatus())
                .createdAt(program.getCreatedAt())
                .build();
    }
}
