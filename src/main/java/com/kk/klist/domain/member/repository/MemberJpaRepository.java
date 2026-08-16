package com.kk.klist.domain.member.repository;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauthProviderAndOauthId(OAuthProvider oauthProvider, String oauthId);
}
