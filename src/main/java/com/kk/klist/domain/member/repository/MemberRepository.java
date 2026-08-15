package com.kk.klist.domain.member.repository;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findByOauthProviderAndOauthId(OAuthProvider oauthProvider, String oauthId);

    Member save(Member member);
}
