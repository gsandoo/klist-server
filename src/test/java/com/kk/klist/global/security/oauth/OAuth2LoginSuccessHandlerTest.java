package com.kk.klist.global.security.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.kk.klist.global.security.auth.Role;
import com.kk.klist.global.security.jwt.JwtTokenProvider;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    private static final String CLIENT_REDIRECT_URI = "http://localhost:5173/oauth/callback";

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private OAuth2LoginSuccessHandler successHandler;

    @Test
    @DisplayName("로그인에 성공하면 토큰을 발급하고 accessToken/refreshToken/isNewMember를 담은 fragment로 리다이렉트한다")
    void onAuthenticationSuccess_whenAuthenticated_redirectsWithTokenFragment() throws IOException {
        // given
        successHandler = new OAuth2LoginSuccessHandler(jwtTokenProvider, CLIENT_REDIRECT_URI);
        OAuthLoginPrincipal principal = new OAuthLoginPrincipal(1L, Role.USER, true);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);
        given(jwtTokenProvider.createAccessToken(1L, Role.USER)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refresh-token");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        assertThat(response.getRedirectedUrl())
                .isEqualTo(CLIENT_REDIRECT_URI + "#accessToken=access-token&refreshToken=refresh-token&isNewMember=true");
    }
}
