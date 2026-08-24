package com.kk.klist.global.security.oauth;

import com.kk.klist.domain.auth.service.AuthService;
import com.kk.klist.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final String clientRedirectUri;

    public OAuth2LoginSuccessHandler(
            JwtTokenProvider jwtTokenProvider,
            AuthService authService,
            @Value("${oauth.client-redirect-uri}") String clientRedirectUri) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.clientRedirectUri = clientRedirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuthLoginPrincipal principal = (OAuthLoginPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.createAccessToken(principal.getMemberId(), principal.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(principal.getMemberId());

        authService.saveRefreshToken(principal.getMemberId(), refreshToken);

        String redirectUrl = clientRedirectUri + "#accessToken=" + accessToken
                + "&refreshToken=" + refreshToken
                + "&isNewMember=" + principal.isNewMember();

        response.sendRedirect(redirectUrl);
    }
}
