package com.agriserve.service;

import com.agriserve.dto.request.FarmerRequest;
import com.agriserve.dto.response.FarmerDocumentResponse;
import com.agriserve.dto.response.FarmerResponse;
import com.agriserve.entity.enums.DocumentType;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contract for Farmer Registration & Profile management.
 */
public interface FarmerService {

    FarmerResponse registerFarmer(FarmerRequest request);

    FarmerResponse getFarmerById(Long farmerId);

    Page<FarmerResponse> getAllFarmers(Pageable pageable);

    Page<FarmerResponse> searchFarmers(String name, String cropType, Status status, Pageable pageable);

    FarmerResponse updateFarmer(Long farmerId, FarmerRequest request);

    void deleteFarmer(Long farmerId);

    FarmerResponse updateFarmerStatus(Long farmerId, Status status);

    // Document operations
    FarmerDocumentResponse uploadDocument(Long farmerId, DocumentType docType, MultipartFile file);

    Page<FarmerDocumentResponse> getFarmerDocuments(Long farmerId, Pageable pageable);

    FarmerDocumentResponse verifyDocument(Long documentId, Status verificationStatus);
}
