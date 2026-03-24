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
public class Report {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long reportId;
	
	@Enumerated(EnumType.STRING)
    private ReportScope scope;
	
	private String metrics;

	@Column(nullable=false)
    private LocalDateTime generatedDate;
    
//    @PrePersist
//    protected void onGenerate() {
//        generatedDate = LocalDateTime.now();
//    }

    public enum ReportScope {
      Farmer,  Program, Feedback
    }
	
}
