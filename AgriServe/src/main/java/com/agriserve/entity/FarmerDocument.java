package com.agriserve.entity;

import com.agriserve.entity.enums.DocumentType;
import com.agriserve.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * KYC / verification documents uploaded by or on behalf of a farmer.
 */
@Entity
@Table(name = "farmer_documents", indexes = {
    @Index(name = "idx_doc_farmer_id", columnList = "farmer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 50)
    private DocumentType docType;

    /** Filesystem or cloud storage URI to the uploaded file */
    @Column(name = "file_uri", nullable = false, length = 500)
    private String fileUri;

    @CreationTimestamp
    @Column(name = "uploaded_date", updatable = false)
    private LocalDateTime uploadedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private Status verificationStatus = Status.PENDING;
}
