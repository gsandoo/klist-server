package com.kk.klist.global.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.kk.klist.global.security.auth.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String SECRET = "unit-test-jwt-secret-key-must-be-32-bytes-min";
    private static final long ACCESS_TOKEN_EXPIRATION = 3_600_000L;
    private static final long REFRESH_TOKEN_EXPIRATION = 1_209_600_000L;

    private final JwtTokenProvider jwtTokenProvider =
            new JwtTokenProvider(SECRET, ACCESS_TOKEN_EXPIRATION, REFRESH_TOKEN_EXPIRATION);

    @Test
    @DisplayName("Access Token을 발급하면 memberId와 role을 포함하고 검증에 성공한다")
    void createAccessToken_whenValid_containsMemberIdAndRole() {
        // given
        Long memberId = 1L;

        // when
        String accessToken = jwtTokenProvider.createAccessToken(memberId, Role.USER);

        // then
        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
        assertThat(jwtTokenProvider.getMemberId(accessToken)).isEqualTo(memberId);
        assertThat(jwtTokenProvider.getRole(accessToken)).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Refresh Token을 발급하면 memberId를 포함하고 검증에 성공한다")
    void createRefreshToken_whenValid_containsMemberId() {
        // given
        Long memberId = 1L;

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        // then
        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
        assertThat(jwtTokenProvider.getMemberId(refreshToken)).isEqualTo(memberId);
    }

    @Test
    @DisplayName("만료된 토큰을 검증하면 실패한다")
    void validateToken_whenExpired_returnsFalse() {
        // given
        String expiredToken = Jwts.builder()
                .subject("1")
                .issuedAt(new Date(System.currentTimeMillis() - 2_000))
                .expiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        // when
        boolean result = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("서명이 일치하지 않는 토큰을 검증하면 실패한다")
    void validateToken_whenSignatureMismatch_returnsFalse() {
        // given
        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "another-unit-test-jwt-secret-key-different-value",
                ACCESS_TOKEN_EXPIRATION,
                REFRESH_TOKEN_EXPIRATION);
        String tokenFromOtherProvider = otherProvider.createAccessToken(1L, Role.USER);

        // when
        boolean result = jwtTokenProvider.validateToken(tokenFromOtherProvider);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("형식이 올바르지 않은 토큰을 검증하면 실패한다")
    void validateToken_whenMalformed_returnsFalse() {
        // when
        boolean result = jwtTokenProvider.validateToken("not-a-valid-jwt-token");

        // then
        assertThat(result).isFalse();
    }
}
