package com.agriserve.service;

import com.agriserve.dto.request.LoginRequest;
import com.agriserve.dto.request.RegisterRequest;
import com.agriserve.dto.response.AuthResponse;
import com.agriserve.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contract for Identity & Access Management operations.
 */
public interface AuthService {

    AuthResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long userId);

    void deactivateUser(Long userId);
}
