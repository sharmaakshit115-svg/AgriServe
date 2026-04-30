package com.agriserve.service;

import com.agriserve.entity.User;

/**
 * Service for recording audit log entries for significant actions.
 */
public interface AuditLogService {

    void log(User user, String action, String resource, String details);

    void log(Long userId, String action, String resource);
}
