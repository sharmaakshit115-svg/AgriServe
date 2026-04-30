package com.agriserve.repository;

import com.agriserve.entity.SatisfactionMetric;
import com.agriserve.entity.TrainingProgram;
import com.agriserve.entity.Workshop;
import com.agriserve.entity.enums.MetricType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SatisfactionMetricRepository extends JpaRepository<SatisfactionMetric, Long> {


    Optional<SatisfactionMetric> findByProgramAndMetricType(TrainingProgram program, MetricType metricType);

    Optional<SatisfactionMetric> findByWorkshopAndMetricType(Workshop workshop, MetricType metricType);

    List<SatisfactionMetric> findAllByProgram_ProgramId(Long programId);

    /** Find the latest metric snapshot for a program of a given type */
    Optional<SatisfactionMetric> findTopByProgram_ProgramIdAndMetricTypeOrderByComputedDateDesc(
            Long programId, MetricType metricType);

    /** Find all officer performance metrics for a specific officer */
    List<SatisfactionMetric> findAllByOfficer_UserIdAndMetricType(Long officerId, MetricType metricType);
}
