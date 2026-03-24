package com.agriservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="training_program")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgram {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long programId;

	    @Column(nullable=false,length=150)
	    private String title;

	    private String description;

	    private LocalDate startDate;
	    
	    private LocalDate endDate;

	    @Enumerated(EnumType.STRING)
	    private TrainingStatus status;
	    
	public enum TrainingStatus{
       PLANNED,ONGOING,COMPLETED,CANCELLED
	}

}
