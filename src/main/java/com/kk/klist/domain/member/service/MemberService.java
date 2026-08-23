package com.kk.klist.domain.member.service;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import com.kk.klist.domain.member.repository.MemberRepository;
import com.kk.klist.global.exception.AuthErrorCode;
import com.kk.klist.global.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));
    }

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
