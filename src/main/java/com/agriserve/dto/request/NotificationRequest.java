package com.agriserve.dto.request;

import com.agriserve.entity.enums.NotificationCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for creating a notification.
 */
@Data
public class NotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long entityId;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Category is required")
    private NotificationCategory category;
}
