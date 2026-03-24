package com.agriservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="farmer_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long documentId;
	
	@ManyToOne
	@JoinColumn(name = "farmer_id", nullable = false)
	private Farmer farmer;
	
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private DocumentType docType;

    @Column(nullable = false, length=200)
    private String fileUri;

    @Column(nullable=false)
    private LocalDateTime uploadedDate;
    

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private VerificationStatus verificationStatus;

//    @PrePersist
//    protected void onUpload() {
//        uploadedDate = LocalDateTime.now();
//    }

      public enum DocumentType {
          IDPROOF,LANDRECORD
       }


       public enum VerificationStatus {
         VERIFIED,PENDING,REJECTED
     }

}
