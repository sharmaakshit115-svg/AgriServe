package com.agriserve.controller;

import com.agriserve.entity.SatisfactionMetric;
import com.agriserve.repository.SatisfactionMetricRepository;
import com.agriserve.service.SatisfactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class SatisfactionMetricController {

    private final SatisfactionService satisfactionService;

    private final SatisfactionMetricRepository metricRepo;

    @GetMapping("/program/{programId}")
    public ResponseEntity<List<SatisfactionMetric>> getProgramMetrics(@PathVariable Long programId) {
        List<SatisfactionMetric> history = metricRepo.findByProgram_ProgramIdOrderByDateDesc(programId);

        if (history.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(history);
    }

    @PostMapping("/workshop/{workshopId}/recalculate")
    public ResponseEntity<String> recalculateScore(@PathVariable Long workshopId) {
        satisfactionService.updateProgramSatisfactionScore(workshopId);
        return ResponseEntity.ok("Satisfaction metric updated successfully.");
    }
}
