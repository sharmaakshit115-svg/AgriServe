package com.agriserve.entity;

import com.agriserve.entity.enums.NotificationCategory;
import com.agriserve.entity.enums.NotificationEntityType;
import com.agriserve.entity.enums.NotificationStatus;
import com.agriserve.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * In-app notification sent to a specific user.
 * entityId optionally references the related business object.
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notif_user", columnList = "user_id"),
    @Index(name = "idx_notif_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Optional reference to the related entity (farmer ID, session ID, etc.) */
    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name="entity_type",length = 30)
    private NotificationEntityType entityType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.UNREAD;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
