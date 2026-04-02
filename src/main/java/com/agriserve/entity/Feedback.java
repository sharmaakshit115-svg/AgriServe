package com.agriserve.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private AdvisorySession session;


    @Column(nullable = false,updatable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String comments;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime date;

}
