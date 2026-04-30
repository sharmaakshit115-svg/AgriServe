package com.agriserve.controller;

import com.agriserve.dto.request.LoginRequest;
import com.agriserve.dto.request.RegisterRequest;
import com.agriserve.dto.response.ApiResponse;
import com.agriserve.dto.response.AuthResponse;
import com.agriserve.dto.response.UserResponse;
import com.agriserve.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Identity & Access Management.
 * Public: /auth/login, /auth/register
 * Protected: /users/** (ADMIN only)
 */
@Tag(name = "Authentication", description = "User registration, login, and management")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully"));
    }

    @Operation(summary = "Login and obtain a JWT token")
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(auth, "Login successful"));
    }

    @Operation(summary = "Get all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(authService.getAllUsers(pageable)));
    }

    @Operation(summary = "Get user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(authService.getUserById(userId)));
    }

    @Operation(summary = "Deactivate a user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long userId) {
        authService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User deactivated"));
    }
}
