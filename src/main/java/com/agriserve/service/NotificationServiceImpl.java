package com.agriserve.service;

import com.agriserve.entity.Notification;
import com.agriserve.entity.NotificationCategory;
import com.agriserve.entity.User;
import com.agriserve.repository.NotificationRepository;
import com.agriserve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public Notification sendNotification(Long userId,
                                         Long entityId,
                                         String message,
                                         NotificationCategory category) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification n = new Notification();
        n.setUser(user);
        n.setEntityId(entityId);
        n.setMessage(message);
        n.setCategory(category);
        n.setStatus("SENT");
        n.setCreatedDate(LocalDateTime.now());

        return repo.save(n);
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return repo.findByUser_UserId(userId);
    }
}
