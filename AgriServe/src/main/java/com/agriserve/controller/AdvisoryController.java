package com.agriserve.controller;

import com.agriserve.dto.request.AdvisoryContentRequest;
import com.agriserve.dto.request.AdvisorySessionRequest;
import com.agriserve.dto.response.AdvisoryContentResponse;
import com.agriserve.dto.response.AdvisorySessionResponse;
import com.agriserve.dto.response.ApiResponse;
import com.agriserve.entity.enums.AdvisoryCategory;
import com.agriserve.entity.enums.Status;
import com.agriserve.service.AdvisoryService;
import com.agriserve.security.SecurityService;
import com.agriserve.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Advisory Content & Advisory Session management.
 */
@Tag(name = "Advisory", description = "Advisory content CRUD and session booking")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/advisory")
@RequiredArgsConstructor
public class AdvisoryController {

    private final AdvisoryService advisoryService;
    private final SecurityService securityService;

    // ─── Advisory Content ─────────────────────────────────────────────────────

    @Operation(summary = "Create advisory content (Officer / Admin)")
    @PostMapping("/content")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AdvisoryContentResponse>> createContent(
            @Valid @RequestBody AdvisoryContentRequest request) {
        Long authorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(advisoryService.createContent(request, authorId),
                        "Advisory content created"));
    }

    @Operation(summary = "Search advisory content with filters")
    @GetMapping("/content")
    public ResponseEntity<ApiResponse<Page<AdvisoryContentResponse>>> searchContent(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) AdvisoryCategory category,
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 20, sort = "uploadedDate") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                advisoryService.searchContent(title, category, status, pageable)));
    }

    @Operation(summary = "Get advisory content by ID")
    @GetMapping("/content/{contentId}")
    public ResponseEntity<ApiResponse<AdvisoryContentResponse>> getContent(@PathVariable Long contentId) {
        return ResponseEntity.ok(ApiResponse.success(advisoryService.getContentById(contentId)));
    }

    @Operation(summary = "Update advisory content (Officer / Admin)")
    @PutMapping("/content/{contentId}")
    @PreAuthorize("hasAnyRole('EXTENSION_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AdvisoryContentResponse>> updateContent(
            @PathVariable Long contentId,
            @Valid @RequestBody AdvisoryContentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                advisoryService.updateContent(contentId, request), "Advisory content updated"));
    }

    @Operation(summary = "Delete advisory content (Admin only)")
    @DeleteMapping("/content/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable Long contentId) {
        advisoryService.deleteContent(contentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Advisory content deleted"));
    }

    // ─── Advisory Sessions ────────────────────────────────────────────────────

    @Operation(summary = "Book an advisory session (Farmer, Officer, or Admin)")
    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('FARMER', 'EXTENSION_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AdvisorySessionResponse>> createSession(
            @Valid @RequestBody AdvisorySessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(advisoryService.createSession(request), "Session Scheduled"));
    }

    @Operation(summary = "Get advisory session by ID")
    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAM_MANAGER') or @securityService.canAccessSession(#sessionId)")
    public ResponseEntity<ApiResponse<AdvisorySessionResponse>> getSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(advisoryService.getSessionById(sessionId)));
    }

    @Operation(summary = "Get sessions for a specific farmer")
    @GetMapping("/sessions/farmer/{farmerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAM_MANAGER') or (hasRole('FARMER') and @securityService.isOwner(#farmerId))")
    public ResponseEntity<ApiResponse<Page<AdvisorySessionResponse>>> getSessionsByFarmer(
            @PathVariable Long farmerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(advisoryService.getSessionsByFarmer(farmerId, pageable)));
    }

    @Operation(summary = "Get sessions for a specific officer")
    @GetMapping("/sessions/officer/{officerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAM_MANAGER') or (hasRole('EXTENSION_OFFICER') and #officerId == @securityService.getCurrentUserId())")
    public ResponseEntity<ApiResponse<Page<AdvisorySessionResponse>>> getSessionsByOfficer(
            @PathVariable Long officerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(advisoryService.getSessionsByOfficer(officerId, pageable)));
    }

    @Operation(summary = "Update session status and feedback")
    @PatchMapping("/sessions/{sessionId}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EXTENSION_OFFICER') and @securityService.isAssignedOfficer(#sessionId))")
    public ResponseEntity<ApiResponse<AdvisorySessionResponse>> updateSessionStatus(
            @PathVariable Long sessionId,
            @RequestParam Status status,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(ApiResponse.success(
                advisoryService.updateSessionStatus(sessionId, status, feedback), "Session status updated"));
    }
}
