package com.agriserve.entity;

import com.agriserve.entity.enums.AdvisoryCategory;
import com.agriserve.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Advisory content (documents, videos, guides) created by Extension Officers.
 */
@Entity
@Table(name = "advisory_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisoryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** URI to the actual file (PDF, video, etc.) */
    @Column(name = "file_uri", length = 500)
    private String fileUri;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AdvisoryCategory category;

    @CreationTimestamp
    @Column(name = "uploaded_date", updatable = false)
    private LocalDateTime uploadedDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.DRAFT;

    /** Extension officer who created this content */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    // ─── Relationships ───────────────────────────────────────────────────────

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AdvisorySession> sessions = new ArrayList<>();
}
