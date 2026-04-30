package com.agriserve.entity;

import com.agriserve.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Individual workshop events held under a TrainingProgram.
 */
@Entity
@Table(name = "workshops", indexes = {
    @Index(name = "idx_workshop_program", columnList = "program_id"),
    @Index(name = "idx_workshop_officer", columnList = "officer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workshop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workshop_id")
    private Long workshopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private TrainingProgram program;

    /** Extension Officer responsible for delivering the workshop */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Column(nullable = false, length = 300)
    private String location;

    @Column(name = "workshop_date")
    private LocalDateTime workshopDate;

    /**
     * Timestamp when the workshop was marked COMPLETED.
     * Metric calculation is delayed by 24 hours from this timestamp
     * to give all farmers time to submit their ratings.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "metrics_calculated", nullable = false)
    private boolean metricsCalculated = false;

    // ─── Relationships ───────────────────────────────────────────────────────

    @OneToMany(mappedBy = "workshop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();
}
