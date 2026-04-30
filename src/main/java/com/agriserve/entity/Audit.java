package com.agriserve.entity;

import com.agriserve.entity.enums.AuditScope;
import com.agriserve.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Formal government/compliance audit conducted by an auditor.
 * Distinct from the AuditLog (system action log) — this is an official audit record.
 */
@Entity
@Table(name = "audits", indexes = {
    @Index(name = "idx_audit_officer", columnList = "officer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    /** Auditor (Government Auditor or Compliance Officer) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditScope scope;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @CreationTimestamp
    @Column(name = "audit_date", updatable = false)
    private LocalDateTime auditDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PENDING;
}
