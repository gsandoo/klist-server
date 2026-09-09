package com.kk.klist.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.fixture.MemberFixture;
import com.kk.klist.domain.member.service.MemberService;
import com.kk.klist.global.exception.AuthErrorCode;
import com.kk.klist.global.exception.AuthException;
import com.kk.klist.global.security.auth.Role;
import com.kk.klist.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private MemberService memberService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(jwtTokenProvider, memberService);
    }

    @Test
    @DisplayName("유효한 Refresh Token으로 refresh 호출 시 새 Access Token을 반환하고 RT를 rotate한다")
    void refresh_whenValidToken_returnsNewAccessTokenAndRotatesRefreshToken() {
        // given
        String oldRefreshToken = "old-refresh-token";
        String tokenId = "test-token-id";
        Member member = MemberFixture.kakaoMember();
        member.updateRefreshToken(tokenId);

        given(jwtTokenProvider.getMemberId(oldRefreshToken)).willReturn(1L);
        given(jwtTokenProvider.getTokenId(oldRefreshToken)).willReturn(tokenId);
        given(memberService.getById(1L)).willReturn(member);
        given(jwtTokenProvider.createAccessToken(1L, Role.USER)).willReturn("new-access-token");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn("new-refresh-token");
        given(jwtTokenProvider.getTokenId("new-refresh-token")).willReturn("new-token-id");
        given(jwtTokenProvider.getAccessTokenExpirationMillis()).willReturn(3600000L);

        // when
        var result = authService.refresh(oldRefreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.expiresIn()).isEqualTo(3600L);
        assertThat(member.getRefreshTokenId()).isEqualTo("new-token-id");
    }

    @Test
    @DisplayName("만료된 Refresh Token으로 refresh 호출 시 EXPIRED_REFRESH_TOKEN 예외를 던진다")
    void refresh_whenExpiredToken_throwsExpiredRefreshTokenException() {
        // given
        String expiredToken = "expired-refresh-token";
        given(jwtTokenProvider.getMemberId(expiredToken)).willThrow(ExpiredJwtException.class);

        // when & then
        assertThatThrownBy(() -> authService.refresh(expiredToken))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> assertThat(((AuthException) ex).getErrorCode().getCode())
                        .isEqualTo(AuthErrorCode.EXPIRED_REFRESH_TOKEN.getCode()));
    }

    @Test
    @DisplayName("DB의 tokenId와 일치하지 않는 Refresh Token으로 refresh 호출 시 INVALID_REFRESH_TOKEN 예외를 던진다")
    void refresh_whenTokenIdMismatch_throwsInvalidRefreshTokenException() {
        // given
        String refreshToken = "refresh-token";
        Member member = MemberFixture.kakaoMember();
        member.updateRefreshToken("stored-token-id");

        given(jwtTokenProvider.getMemberId(refreshToken)).willReturn(1L);
        given(jwtTokenProvider.getTokenId(refreshToken)).willReturn("different-token-id");
        given(memberService.getById(1L)).willReturn(member);

        // when & then
        assertThatThrownBy(() -> authService.refresh(refreshToken))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> assertThat(((AuthException) ex).getErrorCode().getCode())
                        .isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN.getCode()));
    }

    @Test
    @DisplayName("logout 호출 시 refreshTokenId를 null로 초기화한다")
    void logout_clearsRefreshTokenId() {
        // given
        Member member = MemberFixture.kakaoMember();
        member.updateRefreshToken("some-token-id");
        given(memberService.getById(1L)).willReturn(member);

        // when
        authService.logout(1L);

        // then
        assertThat(member.getRefreshTokenId()).isNull();
    }
}
