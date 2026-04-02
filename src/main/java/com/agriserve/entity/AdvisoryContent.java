package com.agriserve.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "advisory_content")
public class AdvisoryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    private String title;

    @Column(length = 2000)
    private String description;

    private String fileUri;

    @Enumerated(EnumType.STRING)
    private ContentCategory category;

    @CreationTimestamp
    private LocalDateTime uploadedDate;

    private String status;  // ACTIVE, ARCHIVED
}