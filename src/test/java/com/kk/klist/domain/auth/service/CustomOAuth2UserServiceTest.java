package com.kk.klist.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import com.kk.klist.domain.member.fixture.MemberFixture;
import com.kk.klist.domain.member.service.MemberService;
import com.kk.klist.domain.member.service.MemberUpsertResult;
import com.kk.klist.global.security.auth.Role;
import com.kk.klist.global.security.oauth.OAuthLoginPrincipal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @Mock
    private MemberService memberService;

    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    @DisplayName("신규 카카오 로그인이면 Member를 생성하고 isNewMember=true인 OAuthLoginPrincipal을 반환한다")
    void loadUser_whenNewKakaoMember_returnsPrincipalWithIsNewMemberTrue() {
        // given
        customOAuth2UserService = new CustomOAuth2UserService(delegate, memberService);
        OAuth2UserRequest userRequest = kakaoUserRequest();
        given(delegate.loadUser(userRequest)).willReturn(kakaoOAuth2User());
        Member member = MemberFixture.kakaoMemberWithOauthId("123456789");
        given(memberService.upsertMember(
                OAuthProvider.KAKAO, "123456789", "여행자1234", "https://example.com/profile.jpg"))
                .willReturn(new MemberUpsertResult(member, true));

        // when
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // then
        OAuthLoginPrincipal principal = (OAuthLoginPrincipal) result;
        assertThat(principal.getMemberId()).isEqualTo(member.getId());
        assertThat(principal.getRole()).isEqualTo(Role.USER);
        assertThat(principal.isNewMember()).isTrue();
    }

    @Test
    @DisplayName("지원하지 않는 provider면 OAuth2AuthenticationException을 던진다")
    void loadUser_whenUnsupportedProvider_throwsOAuth2AuthenticationException() {
        // given
        customOAuth2UserService = new CustomOAuth2UserService(delegate, memberService);
        OAuth2UserRequest userRequest = googleUserRequest();
        given(delegate.loadUser(userRequest)).willReturn(kakaoOAuth2User());

        // when & then
        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    private OAuth2UserRequest kakaoUserRequest() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("kakao")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .build();
        return new OAuth2UserRequest(registration, accessToken());
    }

    private OAuth2UserRequest googleUserRequest() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("google")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                .userNameAttributeName("sub")
                .build();
        return new OAuth2UserRequest(registration, accessToken());
    }

    private OAuth2AccessToken accessToken() {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "token-value", Instant.now(), Instant.now().plusSeconds(3600));
    }

    private DefaultOAuth2User kakaoOAuth2User() {
        Map<String, Object> attributes = Map.of(
                "id", 123456789L,
                "kakao_account", Map.of(
                        "profile", Map.of(
                                "nickname", "여행자1234",
                                "profile_image_url", "https://example.com/profile.jpg")));
        return new DefaultOAuth2User(Collections.emptyList(), attributes, "id");
    }
}
