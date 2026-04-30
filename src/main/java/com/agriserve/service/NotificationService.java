package com.agriserve.service;

import com.agriserve.dto.request.NotificationRequest;
import com.agriserve.dto.response.NotificationResponse;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contract for Notification & Alert management.
 */
public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest request);

    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable);

    int markAllAsRead(Long userId);

    NotificationResponse markAsRead(Long notificationId);
}
