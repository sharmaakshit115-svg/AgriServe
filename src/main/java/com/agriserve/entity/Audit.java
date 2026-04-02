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
public class Audit {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long auditId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="officer_id",nullable=false)
	private User officer;
	
	@Column(nullable=false)
	private String scope;
	
	private String findings;

	@CreationTimestamp
	private LocalDateTime date;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private AuditStatus status;

    public enum AuditStatus {
    OPEN, IN_PROGRESS, CLOSED
  }

}
