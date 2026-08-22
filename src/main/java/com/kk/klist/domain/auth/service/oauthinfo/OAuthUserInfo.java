package com.kk.klist.domain.auth.service.oauthinfo;

import com.kk.klist.domain.member.domain.entity.OAuthProvider;

public interface OAuthUserInfo {

    OAuthProvider getProvider();

    String getOauthId();

    String getNickname();

    String getProfileImageUrl();
}
