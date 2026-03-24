package com.agriservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="satisfaction_metric")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatisfactionMetric {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long metricId;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private TrainingProgram program;
	
	@Column(nullable=false)
	private Double score;
	
	private LocalDateTime date;
	
	@Enumerated(EnumType.STRING)
	private SessionMetricStatus status;
	
	public enum SessionMetricStatus {
    VALID,
    INVALID
   }

}
