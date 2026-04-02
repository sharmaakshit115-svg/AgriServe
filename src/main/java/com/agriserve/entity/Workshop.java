package com.agriserve.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workshop")
public class Workshop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workshopId;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private TrainingProgram program;

    @ManyToOne
    @JoinColumn(name = "officer_id")
    private User officer;

    private String location;
    private LocalDate date;
    private String status;
}