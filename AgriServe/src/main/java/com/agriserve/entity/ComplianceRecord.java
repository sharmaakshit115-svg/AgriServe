package com.agriserve.entity;

import com.agriserve.entity.enums.ComplianceType;
import com.agriserve.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tracks compliance checks for a specific entity (farmer, workshop, program).
 * entityId holds the primary key of the checked record.
 */
@Entity
@Table(name = "compliance_records", indexes = {
    @Index(name = "idx_compliance_entity", columnList = "entity_id, compliance_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compliance_id")
    private Long complianceId;

    /** ID of the entity being checked (Farmer / Workshop / Program) */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_type", nullable = false, length = 50)
    private ComplianceType complianceType;

    /** Outcome: PASSED, FAILED, CONDITIONAL, PENDING */
    @Column(nullable = false, length = 50)
    private String result;

    @CreationTimestamp
    @Column(name = "check_date", updatable = false)
    private LocalDateTime checkDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

}
