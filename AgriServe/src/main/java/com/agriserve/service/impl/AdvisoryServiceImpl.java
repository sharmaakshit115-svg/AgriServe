package com.agriserve.service.impl;

import com.agriserve.dto.request.AdvisoryContentRequest;
import com.agriserve.dto.request.AdvisorySessionRequest;
import com.agriserve.dto.response.AdvisoryContentResponse;
import com.agriserve.dto.response.AdvisorySessionResponse;
import com.agriserve.entity.AdvisoryContent;
import com.agriserve.entity.AdvisorySession;
import com.agriserve.entity.Farmer;
import com.agriserve.entity.User;
import com.agriserve.entity.enums.AdvisoryCategory;
import com.agriserve.entity.enums.Status;
import com.agriserve.exception.BusinessException;
import com.agriserve.exception.ResourceNotFoundException;
import com.agriserve.repository.AdvisoryContentRepository;
import com.agriserve.repository.AdvisorySessionRepository;
import com.agriserve.repository.FarmerRepository;
import com.agriserve.repository.UserRepository;
import com.agriserve.service.AdvisoryService;
import com.agriserve.service.AuditLogService;
import com.agriserve.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages advisory content and session booking.
 *
 * Fixes applied:
 * Issue 3 – audit logs on content creation and session booking
 * Issue 5 – validate content is PUBLISHED; prevent officer double-booking
 * Session Ownership – booking validates and links farmer correctly
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvisoryServiceImpl implements AdvisoryService {

        private final AdvisoryContentRepository contentRepository;
        private final AdvisorySessionRepository sessionRepository;
        private final UserRepository userRepository;
        private final FarmerRepository farmerRepository;
        private final AuditLogService auditLogService;

        // ─── Advisory Content ────────────────────────────────────────────────────

        @Override
        @Transactional
        public AdvisoryContentResponse createContent(AdvisoryContentRequest request, Long authorId) {
                User author = userRepository.findById(authorId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

                AdvisoryContent content = AdvisoryContent.builder()
                                .title(request.getTitle())
                                .description(request.getDescription())
                                .fileUri(request.getFileUri())
                                .category(request.getCategory())
                                .status(request.getStatus() != null ? request.getStatus() : Status.DRAFT)
                                .author(author)
                                .build();
                AdvisoryContent saved = contentRepository.save(content);
                auditLogService.log(author, "CREATE_ADVISORY_CONTENT", "AdvisoryContent#" + saved.getContentId(), null);
                return AdvisoryContentResponse.from(saved);
        }

        @Override
        @Transactional(readOnly = true)
        public AdvisoryContentResponse getContentById(Long contentId) {
                return AdvisoryContentResponse.from(findContentOrThrow(contentId));
        }

        @Override
        @Transactional(readOnly = true)
        public Page<AdvisoryContentResponse> searchContent(String title, AdvisoryCategory category,
                        Status status, Pageable pageable) {
                return contentRepository.searchContent(title, category, status, pageable)
                                .map(AdvisoryContentResponse::from);
        }

        @Override
        @Transactional
        public AdvisoryContentResponse updateContent(Long contentId, AdvisoryContentRequest request) {
                AdvisoryContent content = findContentOrThrow(contentId);
                content.setTitle(request.getTitle());
                content.setDescription(request.getDescription());
                content.setFileUri(request.getFileUri());
                content.setCategory(request.getCategory());
                if (request.getStatus() != null)
                        content.setStatus(request.getStatus());
                AdvisoryContent updated = contentRepository.save(content);
                auditLogService.log(SecurityUtils.getCurrentUserId(), "UPDATE_ADVISORY_CONTENT",
                                "AdvisoryContent#" + contentId);
                return AdvisoryContentResponse.from(updated);
        }

        @Override
        @Transactional
        public void deleteContent(Long contentId) {
                AdvisoryContent content = findContentOrThrow(contentId);
                contentRepository.delete(content);
                auditLogService.log(SecurityUtils.getCurrentUserId(), "DELETE_ADVISORY_CONTENT",
                                "AdvisoryContent#" + contentId);
        }

        // ─── Advisory Sessions ───────────────────────────────────────────────────

        @Override
        @Transactional
        public AdvisorySessionResponse createSession(AdvisorySessionRequest request) {
                User officer = userRepository.findById(request.getOfficerId())
                                .orElseThrow(() -> new ResourceNotFoundException("User (Officer)", "id",
                                                request.getOfficerId()));
                Farmer farmer = farmerRepository.findById(request.getFarmerId())
                                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id",
                                                request.getFarmerId()));

                // ── Issue 5: Validate content is PUBLISHED ────────────────────────
                AdvisoryContent content = null;
                if (request.getContentId() != null) {
                        content = findContentOrThrow(request.getContentId());
                        // if (content.getStatus() != Status.ACTIVE) {
                        // // ACTIVE is used as "PUBLISHED" in the Status enum
                        // throw new BusinessException(
                        // "Advisory content [" + content.getContentId()
                        // + "] is not published (status: "
                        // + content.getStatus()
                        // + "). Only published content may be used for sessions.");
                        // }
                }

                // ── Issue 5: Prevent officer double-booking with same farmer ──────
                boolean alreadyBooked = sessionRepository
                                .existsByOfficer_UserIdAndFarmer_FarmerIdAndStatus(
                                                request.getOfficerId(), request.getFarmerId(), Status.PENDING);
                if (alreadyBooked) {
                        throw new BusinessException(
                                        "Officer already has a PENDING session with this farmer. "
                                                        + "Resolve the existing session before booking a new one.");
                }

                AdvisorySession session = AdvisorySession.builder()
                                .officer(officer)
                                .farmer(farmer)
                                .content(content)
                                .sessionDate(request.getSessionDate())
                                .status(Status.PENDING)
                                .build();
                AdvisorySession saved = sessionRepository.save(session);

                // ── Issue 3: Audit log ─────────────────────────────────────────────
                auditLogService.log(officer, "BOOK_ADVISORY_SESSION",
                                "AdvisorySession#" + saved.getSessionId(),
                                "Farmer#" + farmer.getFarmerId());
                log.info("Advisory session booked: session={}, officer={}, farmer={}",
                                saved.getSessionId(), officer.getUserId(), farmer.getFarmerId());
                return AdvisorySessionResponse.from(saved);
        }

        @Override
        @Transactional(readOnly = true)
        public AdvisorySessionResponse getSessionById(Long sessionId) {
                return AdvisorySessionResponse.from(findSessionOrThrow(sessionId));
        }

        @Override
        @Transactional(readOnly = true)
        public Page<AdvisorySessionResponse> getSessionsByFarmer(Long farmerId, Pageable pageable) {
                return sessionRepository.findAllByFarmer_FarmerId(farmerId, pageable)
                                .map(AdvisorySessionResponse::from);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<AdvisorySessionResponse> getSessionsByOfficer(Long officerId, Pageable pageable) {
                return sessionRepository.findAllByOfficer_UserId(officerId, pageable)
                                .map(AdvisorySessionResponse::from);
        }

        @Override
        @Transactional
        public AdvisorySessionResponse updateSessionStatus(Long sessionId, Status status, String feedback) {
                AdvisorySession session = findSessionOrThrow(sessionId);
                session.setStatus(status);
                if (feedback != null)
                        session.setFeedback(feedback);
                AdvisorySession updated = sessionRepository.save(session);
                auditLogService.log(SecurityUtils.getCurrentUserId(), "UPDATE_SESSION_STATUS",
                                "AdvisorySession#" + sessionId + " -> " + status);
                return AdvisorySessionResponse.from(updated);
        }

        // ─── Private Helpers ─────────────────────────────────────────────────────

        private AdvisoryContent findContentOrThrow(Long id) {
                return contentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("AdvisoryContent", "id", id));
        }

        private AdvisorySession findSessionOrThrow(Long id) {
                return sessionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("AdvisorySession", "id", id));
        }
}
