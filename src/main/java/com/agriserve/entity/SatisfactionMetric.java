package com.agriserve.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SatisfactionMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long metricId;

    @ManyToOne
    @JoinColumn(name="program_id",nullable = false)
    private TrainingProgram program;

    @Column(nullable = false)
    private Double averageScore;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SatisfactionMetricStatus status;

    public enum SatisfactionMetricStatus {
        ACTIVE, EXCELLENT, STABLE,
        NEEDS_IMPROVEMENT, INACTIVE
    }

}
