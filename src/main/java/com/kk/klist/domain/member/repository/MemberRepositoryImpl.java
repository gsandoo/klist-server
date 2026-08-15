package com.kk.klist.domain.member.repository;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findByOauthProviderAndOauthId(OAuthProvider oauthProvider, String oauthId) {
        return memberJpaRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId);
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }
}
