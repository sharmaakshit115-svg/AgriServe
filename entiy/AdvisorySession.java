package com.agriservice.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "advisory_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisorySession {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long sessionId;

	    @ManyToOne
	    @JoinColumn(name = "officer_id",nullable=false)
	    private User officer;

	    @ManyToOne
	    @JoinColumn(name = "farmer_id",nullable=false)
	    private Farmer farmer;

	    @ManyToOne
	    @JoinColumn(name = "content_id",nullable=false)
	    private AdvisoryContent content;

	    @Column(nullable=false)
	    private LocalDateTime date;

	    @Column(columnDefinition = "TEXT")
	    private String feedback;

	    @Enumerated(EnumType.STRING)
	    private AdvisorySessionStatus status;
    
    public enum AdvisorySessionStatus {
        SCHEDULED,COMPLETED,CANCELLED
    }

}