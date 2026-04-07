package com.taskmanager.service;

import com.taskmanager.dto.request.*;
import com.taskmanager.dto.response.AuthResponse;
import com.taskmanager.dto.response.MessageResponse;
import com.taskmanager.entity.RefreshToken;
import com.taskmanager.entity.User;
import com.taskmanager.entity.UserConfig;
import com.taskmanager.entity.UserPassword;
import com.taskmanager.exception.BadRequestException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.RefreshTokenRepository;
import com.taskmanager.repository.UserPasswordRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .userConfig(new UserConfig())
                .build();
        user = userRepository.save(user);

        UserPassword userPassword = UserPassword.builder()
                .userId(user.getId())
                .encryptedPassword(passwordEncoder.encode(request.getPassword()))
                .build();
        userPasswordRepository.save(userPassword);

        return generateTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        UserPassword userPassword = userPasswordRepository.findById(user.getId())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), userPassword.getEncryptedPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        return generateTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new BadRequestException("Refresh token expired");
        }

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return generateTokens(user);
    }

    @Transactional
    public MessageResponse logout(String userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        return new MessageResponse("Logged out successfully");
    }

    @Transactional
    public MessageResponse updatePassword(String userId, UpdatePasswordRequest request) {
        UserPassword userPassword = userPasswordRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), userPassword.getEncryptedPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        userPassword.setEncryptedPassword(passwordEncoder.encode(request.getNewPassword()));
        userPasswordRepository.save(userPassword);

        // Revoke all refresh tokens on password change
        refreshTokenRepository.revokeAllByUserId(userId);

        return new MessageResponse("Password updated successfully");
    }

    private AuthResponse generateTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());

        String refreshTokenStr = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshTokenStr)
                .expiryDate(ZonedDateTime.now(ZoneOffset.UTC).plus(refreshTokenExpirationMs, ChronoUnit.MILLIS))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenStr, jwtTokenProvider.getAccessTokenExpirationMs());
    }
}
