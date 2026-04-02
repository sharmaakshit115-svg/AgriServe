package com.agriserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.agriserve.entity.SatisfactionMetric;

import java.util.List;

@Repository
public interface SatisfactionMetricRepository extends JpaRepository<SatisfactionMetric, Long> {

    List<SatisfactionMetric> findByProgram_ProgramIdOrderByDateDesc(Long programId);
}
