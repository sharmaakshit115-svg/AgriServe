package com.agriserve.service.impl;

import com.agriserve.entity.SatisfactionMetric;
import com.agriserve.entity.TrainingProgram;
import com.agriserve.entity.User;
import com.agriserve.entity.Workshop;
import com.agriserve.entity.enums.MetricType;
import com.agriserve.entity.enums.Status;
import com.agriserve.repository.FeedbackRepository;
import com.agriserve.repository.ParticipationRepository;
import com.agriserve.repository.SatisfactionMetricRepository;
import com.agriserve.repository.UserRepository;
import com.agriserve.repository.WorkshopRepository;
import com.agriserve.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricSchedulerService {

    private final WorkshopRepository workshopRepository;
    private final ParticipationRepository participationRepository;
    private final FeedbackRepository feedbackRepository;
    private final SatisfactionMetricRepository metricRepository;
    private final AuditLogService auditLogService;

    private static final long METRIC_DELAY_HOURS = 24;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void calculatePendingMetrics() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(METRIC_DELAY_HOURS);

        List<Workshop> newCompletedWorkshops = workshopRepository
                .findByStatusAndMetricsCalculatedFalseAndCompletedAtBefore(Status.COMPLETED, cutoff);

        if (newCompletedWorkshops.isEmpty()) {
            log.debug("MetricScheduler: No new workshops to process.");
            return;
        }

        Set<TrainingProgram> programsToUpdate = new HashSet<>();

        for (Workshop workshop : newCompletedWorkshops) {

            computeWorkshopMetric(workshop);

            workshop.setMetricsCalculated(true);
            workshopRepository.save(workshop);

            if (workshop.getProgram() != null) {
                programsToUpdate.add(workshop.getProgram());
            }
        }

        for (TrainingProgram program : programsToUpdate) {
            computeProgramSatisfactionMetric(program);
        }

        log.info("MetricScheduler: Processed {} workshops and updated {} programs.",
                newCompletedWorkshops.size(), programsToUpdate.size());
    }

    private void computeWorkshopMetric(Workshop workshop) {
        Double avg = participationRepository.findAverageWorkshopRatingByWorkshopId(workshop.getWorkshopId());
        double score = avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;

        SatisfactionMetric metric = metricRepository
                .findByWorkshopAndMetricType(workshop, MetricType.WORKSHOP_SATISFACTION)
                .orElse(new SatisfactionMetric());

        metric.setWorkshop(workshop);
        metric.setProgram(workshop.getProgram());
        metric.setMetricType(MetricType.WORKSHOP_SATISFACTION);
        metric.setScore(score);
        metric.setStatus(Status.ACTIVE);

        metricRepository.save(metric);
        auditLogService.log(null, "WORKSHOP_METRIC_GENERATED",
                "Workshop#" + workshop.getWorkshopId(), "score=" + score);
    }

    private void computeProgramSatisfactionMetric(TrainingProgram program) {
        // This still calculates the average of ALL workshops in the program
        // to ensure the overall program score stays accurate.
        Double avg = participationRepository.findAverageWorkshopRatingByProgramId(program.getProgramId());
        double score = avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;

        SatisfactionMetric metric = metricRepository
                .findByProgramAndMetricType(program, MetricType.PROGRAM_SATISFACTION)
                .orElse(new SatisfactionMetric());

        metric.setProgram(program);
        metric.setMetricType(MetricType.PROGRAM_SATISFACTION);
        metric.setScore(score);
        metric.setStatus(Status.ACTIVE);

        metricRepository.save(metric);
        auditLogService.log(null, "PROGRAM_METRIC_UPDATED",
                "Program#" + program.getProgramId(), "new_avg_score=" + score);
    }
}