package com.agriserve.controller;

import com.agriserve.dto.request.FarmerRequest;
import com.agriserve.dto.response.ApiResponse;
import com.agriserve.dto.response.FarmerDocumentResponse;
import com.agriserve.dto.response.FarmerResponse;
import com.agriserve.entity.enums.DocumentType;
import com.agriserve.entity.enums.Status;
import com.agriserve.service.FarmerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for Farmer Registration & Profile Management.
 */
@Tag(name = "Farmers", description = "Farmer registration, profile, and document management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/farmers")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    // ─── Farmer Profile ────────────────────────────────────────────────────────

    @Operation(summary = "Register a new farmer")
    @PostMapping
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FarmerResponse>> register(
            @Valid @RequestBody FarmerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(farmerService.registerFarmer(request), "Farmer registered successfully"));
    }

    @Operation(summary = "Get all farmers with pagination, sorting, and optional search")
    @GetMapping
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'PROGRAM_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<FarmerResponse>>> getAllFarmers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<FarmerResponse> farmers = farmerService.searchFarmers(name, cropType, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(farmers));
    }

    @Operation(summary = "Get a farmer by ID")
    @GetMapping("/{farmerId}")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'PROGRAM_MANAGER', 'ADMIN') ")
    public ResponseEntity<ApiResponse<FarmerResponse>> getFarmerById(@PathVariable Long farmerId) {
        return ResponseEntity.ok(ApiResponse.success(farmerService.getFarmerById(farmerId)));
    }

    @Operation(summary = "Update a farmer profile")
    @PutMapping("/{farmerId}")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'ADMIN') or @securityService.isOwner(#farmerId)")
    public ResponseEntity<ApiResponse<FarmerResponse>> updateFarmer(
            @PathVariable Long farmerId,
            @Valid @RequestBody FarmerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(farmerService.updateFarmer(farmerId, request), "Farmer updated"));
    }

    @Operation(summary = "Update farmer status (APPROVED / REJECTED / ACTIVE etc.)")
    @PatchMapping("/{farmerId}/status")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FarmerResponse>> updateStatus(
            @PathVariable Long farmerId,
            @RequestParam Status status) {
        return ResponseEntity.ok(ApiResponse.success(farmerService.updateFarmerStatus(farmerId, status)));
    }

    @Operation(summary = "Delete a farmer (Admin only)")
    @DeleteMapping("/{farmerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFarmer(@PathVariable Long farmerId) {
        farmerService.deleteFarmer(farmerId);
        return ResponseEntity.ok(ApiResponse.success(null, "Farmer deleted"));
    }

    // ─── Documents ────────────────────────────────────────────────────────────

    @Operation(summary = "Upload a KYC document for a farmer (multipart/form-data)")
    @PostMapping(value = "/{farmerId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'ADMIN')  or @securityService.isOwner(#farmerId)")
    public ResponseEntity<ApiResponse<FarmerDocumentResponse>> uploadDocument(
            @PathVariable Long farmerId,
            @RequestParam DocumentType docType,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(farmerService.uploadDocument(farmerId, docType, file), "Document uploaded"));
    }

    @Operation(summary = "Get all documents for a farmer (own documents or staff/admin)")
    @GetMapping("/{farmerId}/documents")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'PROGRAM_MANAGER', 'ADMIN') or @securityService.isOwner(#farmerId)")
    public ResponseEntity<ApiResponse<Page<FarmerDocumentResponse>>> getDocuments(
            @PathVariable Long farmerId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(farmerService.getFarmerDocuments(farmerId, pageable)));
    }

    @Operation(summary = "Verify a farmer document (Officer / Admin)")
    @PatchMapping("/documents/{documentId}/verify")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FarmerDocumentResponse>> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam Status verificationStatus) {
        return ResponseEntity.ok(ApiResponse.success(
                farmerService.verifyDocument(documentId, verificationStatus), "Document verification updated"));
    }
}
