package com.agriserve.service.impl;

import com.agriserve.dto.request.FarmerRequest;
import com.agriserve.dto.response.FarmerDocumentResponse;
import com.agriserve.dto.response.FarmerResponse;
import com.agriserve.entity.Farmer;
import com.agriserve.entity.FarmerDocument;
import com.agriserve.entity.User;
import com.agriserve.entity.enums.DocumentType;
import com.agriserve.entity.enums.Status;
import com.agriserve.exception.BusinessException;
import com.agriserve.exception.ResourceNotFoundException;
import com.agriserve.repository.FarmerDocumentRepository;
import com.agriserve.repository.FarmerRepository;
import com.agriserve.repository.UserRepository;
import com.agriserve.service.AuditLogService;
import com.agriserve.service.FarmerService;
import com.agriserve.util.FileStorageUtil;
import com.agriserve.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Manages Farmer profiles and document uploads.
 *
 * Fixes applied:
 * Issue 1 – links Farmer to its User account on registration
 * Issue 3 – audit logs on register / update / delete
 * Issue 4 – duplicate phone + email check before registration
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

    private final FarmerRepository farmerRepository;
    private final FarmerDocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;
    private final AuditLogService auditLogService;

    // ─── Farmer Profile ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public FarmerResponse registerFarmer(FarmerRequest request) {

        // ── 2. Find existing user (by email OR phone) ─────────────────────────
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> userRepository.findByPhone(request.getPhone())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User",
                                "email/phone",
                                request.getEmail() + "/" + request.getPhone())));

        // ── 3. Ensure user does NOT already have a farmer profile ────────────
        if (farmerRepository.existsByUser_UserId(user.getUserId())) {
            throw new BusinessException(
                    "Farmer profile already exists for user id: " + user.getUserId());
        }

        // ── 1. Prevent duplicate farmer by phone/email ────────────────────────
        if (farmerRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(
                    "Farmer already exists with phone: " + request.getPhone());
        }

        if (farmerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Farmer already exists with email: " + request.getEmail());
        }

        // ── 4. Create farmer and link user (immutable link) ──────────────────
        Farmer farmer = Farmer.builder()
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .contactInfo(request.getContactInfo())
                .landSize(request.getLandSize())
                .cropType(request.getCropType())
                .status(Status.PENDING)
                .user(user)
                .build();

        Farmer saved = farmerRepository.save(farmer);

        log.info("Farmer registered with ID: {}, linked to User: {}",
                saved.getFarmerId(), user.getUserId());

        // ── 5. Audit log ─────────────────────────────────────────────────────
        auditLogService.log(user, "REGISTER_FARMER", "Farmer#" + saved.getFarmerId(), null);

        return FarmerResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerResponse getFarmerById(Long farmerId) {
        return FarmerResponse.from(findFarmerOrThrow(farmerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmerResponse> getAllFarmers(Pageable pageable) {
        return farmerRepository.findAll(pageable).map(FarmerResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmerResponse> searchFarmers(String name, String cropType, Status status, Pageable pageable) {
        return farmerRepository.searchFarmers(name, cropType, status, pageable).map(FarmerResponse::from);
    }

    @Override
    @Transactional
    public FarmerResponse updateFarmer(Long farmerId, FarmerRequest request) {
        Farmer farmer = findFarmerOrThrow(farmerId);

        // Phone uniqueness check on update (only if phone is changing)
        if (!farmer.getPhone().equals(request.getPhone())
                && farmerRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Another farmer already uses phone: " + request.getPhone());
        }
        // Email uniqueness check on update (only if email is changing)
        if (!farmer.getEmail().equals(request.getEmail())
                && farmerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Another farmer already uses email: " + request.getEmail());
        }

        farmer.setName(request.getName());
        farmer.setDateOfBirth(request.getDateOfBirth());
        farmer.setGender(request.getGender());
        farmer.setAddress(request.getAddress());
        farmer.setPhone(request.getPhone());
        farmer.setEmail(request.getEmail());
        farmer.setContactInfo(request.getContactInfo());
        farmer.setLandSize(request.getLandSize());
        farmer.setCropType(request.getCropType());

        Farmer updated = farmerRepository.save(farmer);

        log.info("Farmer updated with ID: {}, by User: {}", farmerId, SecurityUtils.getCurrentUserId());

        // ── Issue 3: Audit log ─────────────────────────────────────────────
        auditLogService.log(SecurityUtils.getCurrentUserId(), "UPDATE_FARMER", "Farmer#" + farmerId);
        return FarmerResponse.from(updated);
    }

    @Override
    @Transactional
    public void deleteFarmer(Long farmerId) {
        Farmer farmer = findFarmerOrThrow(farmerId);
        farmerRepository.delete(farmer);
        log.info("Farmer deleted: {}", farmerId);
        // ── Issue 3: Audit log ─────────────────────────────────────────────
        auditLogService.log(SecurityUtils.getCurrentUserId(), "DELETE_FARMER", "Farmer#" + farmerId);
    }

    @Override
    @Transactional
    public FarmerResponse updateFarmerStatus(Long farmerId, Status status) {
        Farmer farmer = findFarmerOrThrow(farmerId);
        farmer.setStatus(status);
        Farmer updated = farmerRepository.save(farmer);
        // ── Issue 3: Audit log ─────────────────────────────────────────────
        auditLogService.log(SecurityUtils.getCurrentUserId(), "UPDATE_FARMER_STATUS",
                "Farmer#" + farmerId + " -> " + status);
        return FarmerResponse.from(updated);
    }

    // ─── Documents ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FarmerDocumentResponse uploadDocument(Long farmerId, DocumentType docType, MultipartFile file) {
        Farmer farmer = findFarmerOrThrow(farmerId);
        String fileUri = fileStorageUtil.storeFile(file, "farmer_" + farmerId);

        FarmerDocument document = FarmerDocument.builder()
                .farmer(farmer)
                .docType(docType)
                .fileUri(fileUri)
                .verificationStatus(Status.PENDING)
                .build();

        FarmerDocument saved = documentRepository.save(document);

        log.info("Document uploaded for Farmer ID: {}, DocType: {}, FileUri: {}",
                farmerId, docType, fileUri);

        // ── Audit log ──────────────────────────────────────────────────────
        auditLogService.log(SecurityUtils.getCurrentUserId(), "UPLOAD_DOCUMENT",
                "Farmer#" + farmerId + " DocType=" + docType + " Doc#" + saved.getDocumentId());

        return FarmerDocumentResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmerDocumentResponse> getFarmerDocuments(Long farmerId, Pageable pageable) {
        findFarmerOrThrow(farmerId);
        return documentRepository.findAllByFarmer_FarmerId(farmerId, pageable)
                .map(FarmerDocumentResponse::from);
    }

    @Override
    @Transactional
    public FarmerDocumentResponse verifyDocument(Long documentId, Status verificationStatus) {
        FarmerDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("FarmerDocument", "id", documentId));
        doc.setVerificationStatus(verificationStatus);

        FarmerDocument updated = documentRepository.save(doc);

        log.info("Document verified: Doc#{}, Status: {}, by User: {}",
                documentId, verificationStatus, SecurityUtils.getCurrentUserId());

        // ── Audit log ──────────────────────────────────────────────────────
        auditLogService.log(SecurityUtils.getCurrentUserId(), "VERIFY_DOCUMENT",
                "Doc#" + documentId + " -> " + verificationStatus);

        return FarmerDocumentResponse.from(updated);
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    private Farmer findFarmerOrThrow(Long farmerId) {
        return farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", farmerId));
    }
}
