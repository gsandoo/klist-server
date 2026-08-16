package com.kk.klist.global.security.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import com.kk.klist.global.security.auth.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthLoginPrincipalTest {

    @Test
    @DisplayName("getName()은 memberId를 문자열로 반환한다")
    void getName_returnsMemberIdAsString() {
        // given
        OAuthLoginPrincipal principal = new OAuthLoginPrincipal(1L, Role.USER, true);

        // when
        String name = principal.getName();

        // then
        assertThat(name).isEqualTo("1");
    }

    @Test
    @DisplayName("getAttributes()와 getAuthorities()는 비어있다 — Member/원본 provider 속성을 노출하지 않는다")
    void getAttributesAndAuthorities_areEmpty() {
        // given
        OAuthLoginPrincipal principal = new OAuthLoginPrincipal(1L, Role.USER, false);

        // when & then
        assertThat(principal.getAttributes()).isEmpty();
        assertThat(principal.getAuthorities()).isEmpty();
    }
}
