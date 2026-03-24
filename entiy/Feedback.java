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
public class Feedback {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long feedBackId;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Farmer farmer;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private AdvisorySession session;
	
	@Column(nullable=false)
	private Integer rating;
	
	private String comments;
	
	@Column(nullable=false)
	private LocalDateTime date;
	
//	@PrePersist
//    protected void onCreate() {
//        date = LocalDateTime.now();
//    }
    
	
}
