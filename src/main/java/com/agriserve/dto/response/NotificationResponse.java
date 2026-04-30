package com.agriserve.dto.response;

import com.agriserve.entity.Notification;
import com.agriserve.entity.enums.NotificationCategory;
import com.agriserve.entity.enums.NotificationEntityType;
import com.agriserve.entity.enums.NotificationStatus;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Notification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private Long userId;
    private Long entityId;
    private NotificationEntityType entityType;
    private String message;
    private NotificationCategory category;
    private NotificationStatus status;
    private LocalDateTime createdDate;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .userId(n.getUser() != null ? n.getUser().getUserId() : null)
                .entityId(n.getEntityId())
                .entityType(n.getEntityType())
                .message(n.getMessage())
                .category(n.getCategory())
                .status(n.getStatus())
                .createdDate(n.getCreatedDate())
                .build();
    }
}
