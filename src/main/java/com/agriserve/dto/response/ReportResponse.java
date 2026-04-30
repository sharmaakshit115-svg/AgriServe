package com.agriserve.dto.response;

import com.agriserve.entity.Report;
import com.agriserve.entity.enums.ReportScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long reportId;
    private ReportScope scope;
    private String title;
    private String metrics;
    private Long generatedById;
    private LocalDateTime generatedDate;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .scope(report.getScope())
                .title(report.getTitle())
                .metrics(report.getMetrics())
                .generatedById(report.getGeneratedBy() != null ? report.getGeneratedBy().getUserId() : null)
                .generatedDate(report.getGeneratedDate())
                .build();
    }
}
