package com.agriserve.service.impl;

import com.agriserve.dto.request.LoginRequest;
import com.agriserve.dto.request.RegisterRequest;
import com.agriserve.dto.response.AuthResponse;
import com.agriserve.dto.response.UserResponse;
import com.agriserve.entity.User;
import com.agriserve.entity.enums.Status;
import com.agriserve.exception.DuplicateResourceException;
import com.agriserve.exception.ResourceNotFoundException;
import com.agriserve.repository.UserRepository;
import com.agriserve.security.JwtTokenProvider;
import com.agriserve.service.AuditLogService;
import com.agriserve.service.AuthService;
import com.agriserve.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration, login, and user management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email address is already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .role(request.getRole())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        auditLogService.log(savedUser, "REGISTER_USER", "User#" + savedUser.getUserId(), "New user registered");
        log.info("New user registered: {} with role {}", savedUser.getEmail(), savedUser.getRole());
        return UserResponse.from(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        auditLogService.log(user, "USER_LOGIN", "User#" + user.getUserId(), "Successful login");
        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(tokenProvider.getExpirationMs())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);

        log.info("User Deactivated : {}",userId);
        auditLogService.log(user, "DEACTIVATE_USER", "User#" + userId, "User deactivated");
    }
}
