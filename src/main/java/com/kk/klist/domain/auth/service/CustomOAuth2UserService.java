package com.kk.klist.domain.auth.service;

import com.kk.klist.domain.auth.service.oauthinfo.GoogleOAuthUserInfo;
import com.kk.klist.domain.auth.service.oauthinfo.KakaoOAuthUserInfo;
import com.kk.klist.domain.auth.service.oauthinfo.OAuthUserInfo;
import com.kk.klist.domain.member.service.MemberService;
import com.kk.klist.domain.member.service.MemberUpsertResult;
import com.kk.klist.global.exception.AuthErrorCode;
import com.kk.klist.global.security.oauth.OAuthLoginPrincipal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;
    private final MemberService memberService;

    @Autowired
    public CustomOAuth2UserService(MemberService memberService) {
        this(new DefaultOAuth2UserService(), memberService);
    }

    CustomOAuth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate, MemberService memberService) {
        this.delegate = delegate;
        this.memberService = memberService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthUserInfo userInfo = resolveUserInfo(registrationId, oAuth2User.getAttributes());

        try {
            MemberUpsertResult result = memberService.upsertMember(
                    userInfo.getProvider(), userInfo.getOauthId(), userInfo.getNickname(),
                    userInfo.getProfileImageUrl());
            return new OAuthLoginPrincipal(
                    result.member().getId(), result.member().getRole(), result.isNewMember());
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw oauthError(AuthErrorCode.OAUTH_AUTHENTICATION_FAILED);
        }
    }

    private OAuthUserInfo resolveUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> new KakaoOAuthUserInfo(attributes);
            case "google" -> new GoogleOAuthUserInfo(attributes);
            default -> throw oauthError(AuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        };
    }

    private OAuth2AuthenticationException oauthError(AuthErrorCode errorCode) {
        return new OAuth2AuthenticationException(
                new OAuth2Error(errorCode.getCode(), errorCode.getMessage(), null));
    }
}
