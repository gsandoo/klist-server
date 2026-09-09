package com.kk.klist.domain.auth.dto.response;

public record TokenResponse(String accessToken, String refreshToken, long expiresIn) {}
