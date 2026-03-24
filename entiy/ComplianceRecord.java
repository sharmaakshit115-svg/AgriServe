package com.agriservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compliance_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(nullable=false)
    private LocalDateTime date;

    public enum ComplianceType {
       Advisory,Training
   }


     public enum ComplianceResult {
         PASS,FAIL,
        WARNING,PENDING
   }

}
