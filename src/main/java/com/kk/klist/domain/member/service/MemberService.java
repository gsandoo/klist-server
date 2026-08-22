package com.kk.klist.domain.member.service;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import com.kk.klist.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberUpsertResult upsertMember(
            OAuthProvider oauthProvider, String oauthId, String nickname, String profileImageUrl) {
        return memberRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .map(member -> new MemberUpsertResult(member, false))
                .orElseGet(() -> new MemberUpsertResult(
                        memberRepository.save(Member.create(nickname, oauthProvider, oauthId, profileImageUrl)),
                        true));
    }
}
