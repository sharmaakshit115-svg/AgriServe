package com.agriserve.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Farmer feedback on an advisory session.
 * One feedback per farmer per session enforced by unique constraint.
 * Rating is strictly 1–5 enforced at both DB and validation layer.
 */
@Entity
@Table(
    name = "feedbacks",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_feedback_farmer_session",
        columnNames = {"farmer_id", "session_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AdvisorySession session;

    /**
     * Rating enforced between 1 and 5.
     * Column check constraint is additional safety at DB level.
     */
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @CreationTimestamp
    @Column(name = "feedback_date", updatable = false)
    private LocalDateTime feedbackDate;
}
