package com.kk.klist.domain.member.service;

import com.kk.klist.domain.auth.client.KakaoAuthClient;
import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.MemberWithdrawReason;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import com.kk.klist.domain.member.domain.exception.MemberErrorCode;
import com.kk.klist.domain.member.domain.exception.MemberException;
import com.kk.klist.domain.member.repository.MemberRepository;
import com.kk.klist.domain.member.repository.MemberWithdrawReasonRepository;
import com.kk.klist.global.exception.AuthErrorCode;
import com.kk.klist.global.exception.AuthException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberWithdrawReasonRepository memberWithdrawReasonRepository;
    private final KakaoAuthClient kakaoAuthClient;

    public Member getById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));
        if (!member.isActive()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        return member;
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

    @Transactional
    public void completeOnboarding(Long memberId, String nickname, String profileImageUrl,
                                   String preferredLanguage, String nationality) {
        Member member = getById(memberId);
        if (member.isOnboarding()) {
            throw new MemberException(MemberErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }
        member.completeOnboarding(nickname, profileImageUrl, preferredLanguage, nationality);
    }

    @Transactional
    public void updateProfile(Long memberId, String nickname, String nationality) {
        Member member = getById(memberId);
        member.updateProfile(nickname, nationality);
    }

    @Transactional
    public String updateProfileImage(Long memberId, String profileImageUrl) {
        Member member = getById(memberId);
        member.updateProfileImage(profileImageUrl);
        return member.getProfileImageUrl();
    }

    @Transactional
    public void updatePreferredLanguage(Long memberId, String preferredLanguage) {
        Member member = getById(memberId);
        member.updatePreferredLanguage(preferredLanguage);
    }

    @Transactional
    public void withdraw(Long memberId, List<String> reasons) {
        Member member = getById(memberId);
        reasons.forEach(reason ->
                memberWithdrawReasonRepository.save(MemberWithdrawReason.of(memberId, reason)));
        member.withdraw();
        if (member.getOauthProvider() == OAuthProvider.KAKAO) {
            try {
                kakaoAuthClient.unlinkUser(member.getOauthId());
            } catch (RestClientException e) {
                log.error("[KakaoUnlink] memberId={} unlink 실패: {}", memberId, e.getMessage());
                throw new MemberException(MemberErrorCode.KAKAO_UNLINK_FAILED);
            }
        }
    }
}
