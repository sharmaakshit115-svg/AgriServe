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
public class Audit {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long auditId;
	
	@ManyToOne
	@JoinColumn(name="officer_id",nullable=false)
	private User officer;
	
	@Column(nullable=false)
	private String scope;
	
	private String findings;
	
	@Column(nullable=false)
	private LocalDateTime date;
	
//	@PrePersist
//    protected void onCreate() {
//        date = LocalDateTime.now();
//    }
//	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private AuditStatus status;

    public enum AuditStatus {
    OPEN,
    IN_PROGRESS,
    CLOSED
  }

}
