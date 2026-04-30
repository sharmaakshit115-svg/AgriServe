package com.agriserve.dto.response;

import com.agriserve.entity.FarmerDocument;
import com.agriserve.entity.enums.DocumentType;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for FarmerDocument.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerDocumentResponse {

    private Long documentId;
    private Long farmerId;
    private DocumentType docType;
    private String fileUri;
    private LocalDateTime uploadedDate;
    private Status verificationStatus;

    public static FarmerDocumentResponse from(FarmerDocument doc) {
        return FarmerDocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .farmerId(doc.getFarmer().getFarmerId())
                .docType(doc.getDocType())
                .fileUri(doc.getFileUri())
                .uploadedDate(doc.getUploadedDate())
                .verificationStatus(doc.getVerificationStatus())
                .build();
    }
}
