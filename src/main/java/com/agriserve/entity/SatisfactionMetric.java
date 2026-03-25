package com.agriserve.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "satisfaction_metric")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SatisfactionMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long metricId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TrainingProgram id;

    @Column(nullable = false)
    private Double score;

    @CreationTimestamp
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private SessionMetricStatus status;

    public enum SessionMetricStatus {
        VALID, INVALID
    }

}
