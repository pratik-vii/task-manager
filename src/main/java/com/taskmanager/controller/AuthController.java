package com.taskmanager.controller;

import com.taskmanager.dto.request.LoginRequest;
import com.taskmanager.dto.request.RefreshTokenRequest;
import com.taskmanager.dto.request.RegisterRequest;
import com.taskmanager.dto.request.UpdatePasswordRequest;
import com.taskmanager.dto.response.AuthResponse;
import com.taskmanager.dto.response.success.SuccessResponse;
import com.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(new SuccessResponse<AuthResponse>()
                .setData(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(new SuccessResponse<AuthResponse>()
                .setData(authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(new SuccessResponse<AuthResponse>()
                .setData(authService.refresh(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Void>> logout(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(new SuccessResponse<Void>()
                .setMessage(authService.logout(userId).getMessage()));
    }

    @PutMapping("/password")
    public ResponseEntity<SuccessResponse<Void>> updatePassword(Authentication authentication,
                                                                 @Valid @RequestBody UpdatePasswordRequest request) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(new SuccessResponse<Void>()
                .setMessage(authService.updatePassword(userId, request).getMessage()));
    }
}
