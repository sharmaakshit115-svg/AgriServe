package com.agriserve.entity;

import com.agriserve.entity.enums.ReportScope;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Generated analytical reports for management and government stakeholders.
 * 'metrics' stores a JSON/text snapshot of the report payload.
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportScope scope;

    /** JSON-serialised metrics snapshot */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String metrics;

    @Column(name = "title", length = 200)
    private String title;

    /** User who triggered the report generation */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by")
    private User generatedBy;

    @CreationTimestamp
    @Column(name = "generated_date", updatable = false)
    private LocalDateTime generatedDate;
}
