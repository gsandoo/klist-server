package com.kk.klist.global.security.oauth;

import com.kk.klist.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * OAuth2 로그인 성공 시 토큰을 발급해 클라이언트로 리다이렉트한다.
 * Refresh Token을 저장소에 영속화하는 책임은 없다 — 발급과 리다이렉트 조립만 담당한다.
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final String clientRedirectUri;

    public OAuth2LoginSuccessHandler(
            JwtTokenProvider jwtTokenProvider,
            @Value("${oauth.client-redirect-uri}") String clientRedirectUri) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.clientRedirectUri = clientRedirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuthLoginPrincipal principal = (OAuthLoginPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.createAccessToken(principal.getMemberId(), principal.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(principal.getMemberId());

        String redirectUrl = clientRedirectUri + "#accessToken=" + accessToken
                + "&refreshToken=" + refreshToken
                + "&isNewMember=" + principal.isNewMember();

        response.sendRedirect(redirectUrl);
    }
}
