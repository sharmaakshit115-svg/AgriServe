package com.agriserve.service.impl;

import com.agriserve.entity.AuditLog;
import com.agriserve.entity.User;
import com.agriserve.repository.AuditLogRepository;
import com.agriserve.repository.UserRepository;
import com.agriserve.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records audit log entries. Called from service methods after significant mutations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void log(User user, String action, String resource, String details) {
        AuditLog entry = AuditLog.builder()
                .user(user)
                .action(action)
                .resource(resource)
                .details(details)
                .build();
        auditLogRepository.save(entry);
        log.debug("Audit log: user={} action={} resource={}", user.getEmail(), action, resource);
    }

    @Override
    @Transactional
    public void log(Long userId, String action, String resource) {
        userRepository.findById(userId).ifPresent(user -> log(user, action, resource, null));
    }
}
