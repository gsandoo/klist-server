package com.kk.klist.domain.auth.service.oauthinfo;

import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import java.util.Map;

public class GoogleOAuthUserInfo implements OAuthUserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public String getOauthId() {
        Object sub = attributes.get("sub");
        if (sub == null) throw new IllegalStateException("Google 사용자 정보에 sub 필드가 없습니다.");
        return String.valueOf(sub);
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) attributes.get("picture");
    }
}
