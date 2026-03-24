package com.agriservice.entity;

import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;


    public enum Role {
	   FARMER, OFFICER, MANAGER, ADMIN, COMPLIANCE, AUDITO
    }
 
     public enum UserStatus {
	    ACTIVE, INACTIVE, SUSPENDED
    }

}



