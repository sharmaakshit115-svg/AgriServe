package com.agriservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"workshop_id","farmer_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationId;

    @ManyToOne
    @JoinColumn(name = "workshop_id",nullable=false)
    private Workshop workshop;

    @ManyToOne
    @JoinColumn(name = "farmer_id",nullable=false)
    private Farmer farmer;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private AttendanceStatus attendanceStatus;

    @Column(length=200)
    private String feedback;
    
    public enum AttendanceStatus {
        INVITED, ATTENDED, ABSENT
    }
}
