package com.kk.klist.global.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * OAuth2 로그인 실패 시 클라이언트로 에러 리다이렉트만 조립한다. Member/도메인 지식을 갖지 않는다.
 */
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final String clientRedirectUri;

    public OAuth2LoginFailureHandler(@Value("${oauth.client-redirect-uri}") String clientRedirectUri) {
        this.clientRedirectUri = clientRedirectUri;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        response.sendRedirect(clientRedirectUri + "?error=oauth_failed");
    }
}
