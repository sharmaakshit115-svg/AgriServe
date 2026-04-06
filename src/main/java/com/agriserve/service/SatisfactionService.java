package com.agriserve.service;

import com.agriserve.entity.SatisfactionMetric;
import com.agriserve.repository.ParticipationRepository;
import com.agriserve.repository.SatisfactionMetricRepository;
import com.agriserve.repository.TrainingProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.agriserve.entity.SatisfactionMetric.SatisfactionMetricStatus; // Ensure this import matches your Enum location
import com.agriserve.entity.TrainingProgram;

@Service
@Transactional
@RequiredArgsConstructor
public class SatisfactionService {

    private final ParticipationRepository participationRepo;
    private final SatisfactionMetricRepository metricRepo;
    private final TrainingProgramRepository programRepo;

    public void updateProgramSatisfactionScore(Long programId) {

        Double averageScore = participationRepo.getAverageScoreByProgram(programId);

        if (averageScore == null) {
            averageScore = 0.0;
        }

        TrainingProgram program = programRepo.findById(programId)
                .orElseThrow(() -> new RuntimeException("Training Program not found with ID: " + programId));

        SatisfactionMetric metric = new SatisfactionMetric();

        metric.setProgram(program);
        metric.setAverageScore(averageScore);

        if (averageScore >= 4.5) {
            metric.setStatus(SatisfactionMetricStatus.EXCELLENT);
        } else if (averageScore >= 3.0) {
            metric.setStatus(SatisfactionMetricStatus.STABLE);
        } else if (averageScore > 0) {
            metric.setStatus(SatisfactionMetricStatus.NEEDS_IMPROVEMENT);
        } else {
            metric.setStatus(SatisfactionMetricStatus.ACTIVE);
        }

        metricRepo.save(metric);
    }
}