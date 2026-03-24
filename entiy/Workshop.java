package com.agriservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workshop {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long workshopId;

	    @ManyToOne
	    @JoinColumn(name = "program_id",nullable=false)
	    private TrainingProgram program;

	    @ManyToOne
	    @JoinColumn(name = "officer_id",nullable=false)
	    private User officer;

	    @Column(length=150,nullable=false)
	    private String location;
	    
	    private LocalDateTime date;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable=false)
	    private WorkShopStatus status;
		

	    public enum WorkShopStatus {
	       SCHEDULED,
	       COMPLETED,
	       CANCELLED
	   }
}
