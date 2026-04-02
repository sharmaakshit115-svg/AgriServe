package com.agriserve.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complianceId;
    
    @Column(nullable = false)
    private Long entityId;
    
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ComplianceType type;
    

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ComplianceResult result;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(nullable=false,updatable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User extensionOfficer;

    public enum ComplianceType {
       ADVISORY,TRAINING
   }


     public enum ComplianceResult {
         PASS,FAIL,
        WARNING,PENDING
   }

}
