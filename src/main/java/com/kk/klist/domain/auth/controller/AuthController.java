package com.kk.klist.domain.auth.controller;

import com.kk.klist.domain.auth.dto.request.OnboardingRequest;
import com.kk.klist.domain.auth.dto.request.RefreshRequest;
import com.kk.klist.domain.auth.dto.response.TokenResponse;
import com.kk.klist.domain.auth.service.AuthService;
import com.kk.klist.global.response.ApiResponse;
import com.kk.klist.global.security.auth.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request.refreshToken())));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@LoginUser Long memberId) {
        authService.logout(memberId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/onboarding")
    public ResponseEntity<Void> onboarding(@LoginUser Long memberId, @Valid @RequestBody OnboardingRequest request) {
        authService.completeOnboarding(memberId, request);
        return ResponseEntity.noContent().build();
    }
}
