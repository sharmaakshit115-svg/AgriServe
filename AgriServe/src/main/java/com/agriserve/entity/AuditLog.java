package com.agriserve.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Records every significant action performed by a user for audit trail purposes.
 * Automatically populated by AuditLogService via AOP or service layer calls.
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Short description of the action (e.g. CREATE_FARMER, UPDATE_ADVISORY) */
    @Column(nullable = false, length = 100)
    private String action;

    /** The resource affected (e.g. "Farmer#42", "AdvisoryContent#7") */
    @Column(nullable = false, length = 200)
    private String resource;

    @Column(columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
}
