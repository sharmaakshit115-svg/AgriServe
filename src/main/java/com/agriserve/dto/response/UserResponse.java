package com.agriserve.dto.response;

import com.agriserve.entity.User;
import com.agriserve.entity.enums.Role;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for User — never exposes the hashed password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String name;
    private Role role;
    private String email;
    private String phone;
    private Status status;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
