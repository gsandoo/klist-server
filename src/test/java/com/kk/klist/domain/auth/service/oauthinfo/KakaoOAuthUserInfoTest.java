package com.kk.klist.domain.auth.service.oauthinfo;

import static org.assertj.core.api.Assertions.assertThat;

import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KakaoOAuthUserInfoTest {

    @Test
    @DisplayName("카카오 응답에서 id/닉네임/프로필 이미지를 파싱한다")
    void constructor_whenAttributesHaveProfile_parsesFields() {
        // given
        Map<String, Object> attributes = Map.of(
                "id", 123456789L,
                "kakao_account", Map.of(
                        "profile", Map.of(
                                "nickname", "여행자1234",
                                "profile_image_url", "https://example.com/profile.jpg")));

        // when
        KakaoOAuthUserInfo userInfo = new KakaoOAuthUserInfo(attributes);

        // then
        assertThat(userInfo.getProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(userInfo.getOauthId()).isEqualTo("123456789");
        assertThat(userInfo.getNickname()).isEqualTo("여행자1234");
        assertThat(userInfo.getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
    }

    @Test
    @DisplayName("동의 항목에 없어 kakao_account가 비어있어도 예외 없이 null을 반환한다")
    void constructor_whenKakaoAccountMissing_returnsNullFields() {
        // given
        Map<String, Object> attributes = Map.of("id", 123456789L);

        // when
        KakaoOAuthUserInfo userInfo = new KakaoOAuthUserInfo(attributes);

        // then
        assertThat(userInfo.getOauthId()).isEqualTo("123456789");
        assertThat(userInfo.getNickname()).isNull();
        assertThat(userInfo.getProfileImageUrl()).isNull();
    }
}
