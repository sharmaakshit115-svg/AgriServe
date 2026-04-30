package com.agriserve.controller;

import com.agriserve.dto.request.NotificationRequest;
import com.agriserve.dto.response.ApiResponse;
import com.agriserve.dto.response.NotificationResponse;
import com.agriserve.service.NotificationService;
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
 * REST controller for Notifications & Alerts management.
 */
@Tag(name = "Notifications", description = "In-app notification management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Send a notification to a user (Admin / Officer)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EXTENSION_OFFICER', 'PROGRAM_MANAGER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> send(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(notificationService.sendNotification(request), "Notification sent"));
    }

    @Operation(summary = "Get all notifications for a user (paginated)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotifications(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUserNotifications(userId, pageable)));
    }

    @Operation(summary = "Get all UNREAD notifications for current logged-in user")
    @GetMapping("/me/unread")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyUnread(
            @PageableDefault(size = 20) Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadNotifications(userId, pageable)));
    }

    @Operation(summary = "Mark a single notification as read")
    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("@SecurityService.isNotificationOwner(#notificationId)")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.markAsRead(notificationId), "Marked as read"));
    }

    @Operation(summary = "Mark ALL notifications as read for current user")
    @PatchMapping("/me/read-all")
    @PreAuthorize("@SecurityService.isNotificationOwner(#notificationId)")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(count, count + " notifications marked as read"));
    }
}
