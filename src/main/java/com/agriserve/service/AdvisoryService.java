package com.agriserve.service;

import com.agriserve.dto.request.AdvisoryContentRequest;
import com.agriserve.dto.request.AdvisorySessionRequest;
import com.agriserve.dto.response.AdvisoryContentResponse;
import com.agriserve.dto.response.AdvisorySessionResponse;
import com.agriserve.entity.enums.AdvisoryCategory;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contract for Advisory Content & Session management.
 */
public interface AdvisoryService {

    // Content
    AdvisoryContentResponse createContent(AdvisoryContentRequest request, Long authorId);

    AdvisoryContentResponse getContentById(Long contentId);

    Page<AdvisoryContentResponse> searchContent(String title, AdvisoryCategory category,
            Status status, Pageable pageable);

    AdvisoryContentResponse updateContent(Long contentId, AdvisoryContentRequest request);

    void deleteContent(Long contentId);

    // Sessions
    AdvisorySessionResponse createSession(AdvisorySessionRequest request);

    AdvisorySessionResponse getSessionById(Long sessionId);

    Page<AdvisorySessionResponse> getSessionsByFarmer(Long farmerId, Pageable pageable);

    Page<AdvisorySessionResponse> getSessionsByOfficer(Long officerId, Pageable pageable);

    AdvisorySessionResponse updateSessionStatus(Long sessionId, Status status, String feedback);
}
