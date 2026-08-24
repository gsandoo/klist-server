package com.kk.klist.domain.member.fixture;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;

public class MemberFixture {

    public static Member kakaoMember() {
        return Member.create("여행자1234", OAuthProvider.KAKAO, "kakao-oauth-id", "https://example.com/profile.jpg");
    }

    public static Member kakaoMemberWithOauthId(String oauthId) {
        return Member.create("여행자1234", OAuthProvider.KAKAO, oauthId, "https://example.com/profile.jpg");
    }
}
