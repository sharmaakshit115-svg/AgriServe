package com.agriserve.service.impl;


import com.agriserve.dto.request.ReportRequest;
import com.agriserve.dto.response.ReportResponse;
import com.agriserve.entity.Audit;
import com.agriserve.entity.Report;
import com.agriserve.entity.User;
import com.agriserve.entity.enums.ReportScope;
import com.agriserve.exception.BusinessException;
import com.agriserve.exception.ResourceNotFoundException;
import com.agriserve.repository.*;
import com.agriserve.service.AuditLogService;
import com.agriserve.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final TrainingProgramRepository programRepository;
    private final AdvisorySessionRepository sessionRepository;
    private final WorkshopRepository workshopRepository;
    private final FeedbackRepository feedbackRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    @Transactional
    public ReportResponse generateReport(ReportRequest request, Long userId) {
        User generatedBy = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        validateRequest(request);

// Build a basic metrics snapshot (in production use JSON serialisation)

        String metrics = buildMetricsSnapshot(request);

        Report report = Report.builder()
                .scope(request.getScope())
                .title(request.getTitle() != null ? request.getTitle() : request.getScope().name() + " Report")
                .metrics(metrics)
                .generatedBy(generatedBy)
                .build();

        Report saved = reportRepository.save(report);

        log.info("Report generated: id={} scope={}", saved.getReportId(), saved.getScope());
        auditLogService.log(userId, "GENERATE_REPORT", "Report#" + saved.getReportId() + ",scope= " + saved.getScope());
        return ReportResponse.from(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long reportId) {
        return ReportResponse.from(reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId)));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getAllReports(ReportScope scope, Pageable pageable) {
        if (scope != null) {
            return reportRepository.findAllByScope(scope, pageable).map(ReportResponse::from);
        }
        return reportRepository.findAll(pageable).map(ReportResponse::from);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getReportByUserId(Long userId, Pageable pageable) {
        return reportRepository.findAllByGeneratedBy_UserId(userId, pageable).map(ReportResponse::from);
    }


// ─── Private ─────────────────────────────────────────────────────────────


    private void validateRequest(ReportRequest request) {

        if (request.getScope() == ReportScope.PROGRAM && request.getProgramId() == null) {
            throw new BusinessException("Program ID is required for PROGRAM_LEVEL report");
        }
    }


    private String buildMetricsSnapshot(ReportRequest request) {

        try {
            Map<String, Object> metrics = new HashMap<>();
            switch (request.getScope()) {

                case GLOBAL:
                    metrics.put("totalFarmers", farmerRepository.count());
                    metrics.put("totalPrograms", programRepository.count());
                    metrics.put("totalSessions", sessionRepository.count());
                    break;


                case PROGRAM:
                    Long programId = request.getProgramId();

                    if (!programRepository.existsById(programId)) {
                        throw new ResourceNotFoundException("Program", "id", programId);
                    }
                    metrics.put("programId", programId);
                    metrics.put("totalWorkshops",

                    workshopRepository.countByProgram_ProgramId(programId));
                    break;

                default:
                    throw new BusinessException("Invalid report scope");

            }
            metrics.put("scope", request.getScope());
            return objectMapper.writeValueAsString(metrics);

        } catch (Exception e) {
            throw new RuntimeException("Error generating report metrics", e);
        }
    }
}