package com.agriserve.repository;

import com.agriserve.entity.Notification;
import com.agriserve.entity.enums.NotificationCategory;
import com.agriserve.entity.enums.NotificationStatus;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByUser_UserId(Long userId, Pageable pageable);

    Page<Notification> findAllByUser_UserIdAndStatus(Long userId, NotificationStatus status, Pageable pageable);

    Page<Notification> findAllByCategory(NotificationCategory category, Pageable pageable);

    /** Mark all notifications for a user as read (ACTIVE = read) */
    @Modifying
    @Query("UPDATE Notification n SET n.status = :readStatus WHERE n.user.userId = :userId AND n.status = :unreadStatus")
    int markAllAsReadForUser(@Param("userId") Long userId, @Param("readStatus") NotificationStatus readStatus, @Param("unreadStatus") NotificationStatus unreadStatus);
}
