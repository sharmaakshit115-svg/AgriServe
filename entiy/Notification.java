package com.agriservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable=false)
    private Long entityId;
    
    @Column(nullable=false)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    
    @Column(nullable=false)
    private LocalDateTime createdDate;

//    @PrePersist
//       protected void onCreate() {
//           createdDate = LocalDateTime.now();
//       }


     public enum NotificationCategory {
        Advisory,Training,
        Feedback,Compliance
     }

     public enum NotificationStatus {
         UNREAD, READ,
         SENT,FAILED
      }

}
