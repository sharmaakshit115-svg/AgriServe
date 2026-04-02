package com.agriserve.entity;

import com.agriserve.entity.Workshop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participation")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationId;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    private String attendanceStatus;
    private String feedback;
}