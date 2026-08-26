package com.kk.klist.domain.auth.service.oauthinfo;

import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import java.util.Map;

public class KakaoOAuthUserInfo implements OAuthUserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> profile;

    @SuppressWarnings("unchecked")
    public KakaoOAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.getOrDefault("kakao_account", Map.of());
        this.profile = (Map<String, Object>) kakaoAccount.getOrDefault("profile", Map.of());
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String getOauthId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getNickname() {
        return null;
    }

    @Override
    public String getProfileImageUrl() {
        return null;
    }
}
