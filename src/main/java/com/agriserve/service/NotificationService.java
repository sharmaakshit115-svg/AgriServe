package com.agriserve.service;

import com.agriserve.entity.Notification;
import com.agriserve.entity.NotificationCategory;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(Long userId,
                                  Long entityId,
                                  String message,
                                  NotificationCategory category);

    List<Notification> getUserNotifications(Long userId);
}