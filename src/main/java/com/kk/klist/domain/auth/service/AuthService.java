package com.kk.klist.domain.auth.service;

import com.kk.klist.domain.auth.dto.request.OnboardingRequest;
import com.kk.klist.domain.auth.dto.response.TokenResponse;
import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.service.MemberService;
import com.kk.klist.global.exception.AuthErrorCode;
import com.kk.klist.global.exception.AuthException;
import com.kk.klist.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Transactional
    public void saveRefreshToken(Long memberId, String refreshToken) {
        Member member = memberService.getById(memberId);
        String tokenId = jwtTokenProvider.getTokenId(refreshToken);
        member.updateRefreshToken(tokenId);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        Long memberId = extractMemberIdFromRefreshToken(refreshToken);
        String incomingTokenId = jwtTokenProvider.getTokenId(refreshToken);

        Member member = memberService.getById(memberId);
        if (member.getRefreshTokenId() == null || !member.getRefreshTokenId().equals(incomingTokenId)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(memberId, member.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);
        member.updateRefreshToken(jwtTokenProvider.getTokenId(newRefreshToken));

        long expiresIn = jwtTokenProvider.getAccessTokenExpirationMillis() / 1000;
        return new TokenResponse(newAccessToken, expiresIn);
    }

    @Transactional
    public void logout(Long memberId) {
        Member member = memberService.getById(memberId);
        member.clearRefreshToken();
    }

    @Transactional
    public void completeOnboarding(Long memberId, OnboardingRequest request) {
        memberService.completeOnboarding(
                memberId,
                request.nickname(),
                null,  // profileImage Base64 → 저장소 업로드 미구현, 추후 S3 연동 시 교체
                request.preferredLanguage(),
                request.nationality()
        );
    }

    private Long extractMemberIdFromRefreshToken(String refreshToken) {
        try {
            return jwtTokenProvider.getMemberId(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
