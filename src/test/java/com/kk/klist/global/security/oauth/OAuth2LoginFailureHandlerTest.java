package com.kk.klist.global.security.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

class OAuth2LoginFailureHandlerTest {

    private static final String CLIENT_REDIRECT_URI = "http://localhost:5173/oauth/callback";

    private final OAuth2LoginFailureHandler failureHandler = new OAuth2LoginFailureHandler(CLIENT_REDIRECT_URI);

    @Test
    @DisplayName("로그인에 실패하면 error=oauth_failed 쿼리로 리다이렉트한다")
    void onAuthenticationFailure_whenAuthenticationFails_redirectsWithErrorQuery() throws IOException {
        // given
        AuthenticationException exception = new BadCredentialsException("oauth login failed");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        assertThat(response.getRedirectedUrl()).isEqualTo(CLIENT_REDIRECT_URI + "?error=oauth_failed");
    }
}
