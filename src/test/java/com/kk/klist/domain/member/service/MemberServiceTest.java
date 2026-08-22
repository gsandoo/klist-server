package com.kk.klist.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.domain.entity.OAuthProvider;
import com.kk.klist.domain.member.fixture.MemberFixture;
import com.kk.klist.domain.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("존재하지 않는 provider+id로 upsert하면 신규 회원이 생성되고 isNewMember=true를 반환한다")
    void upsertMember_whenNotExists_createsNewMember() {
        // given
        String oauthId = "kakao-oauth-id";
        given(memberRepository.findByOauthProviderAndOauthId(OAuthProvider.KAKAO, oauthId))
                .willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberUpsertResult result = memberService.upsertMember(
                OAuthProvider.KAKAO, oauthId, "여행자1234", "https://example.com/profile.jpg");

        // then
        assertThat(result.isNewMember()).isTrue();
        assertThat(result.member().getOauthProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(result.member().getOauthId()).isEqualTo(oauthId);
        assertThat(result.member().getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
        then(memberRepository).should(times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("이미 존재하는 provider+id로 upsert하면 기존 회원을 그대로 반환하고 새로 생성하지 않는다")
    void upsertMember_whenAlreadyExists_returnsExistingMemberWithoutCreating() {
        // given
        String oauthId = "kakao-oauth-id";
        Member existingMember = MemberFixture.kakaoMemberWithOauthId(oauthId);
        given(memberRepository.findByOauthProviderAndOauthId(OAuthProvider.KAKAO, oauthId))
                .willReturn(Optional.of(existingMember));

        // when
        MemberUpsertResult result = memberService.upsertMember(
                OAuthProvider.KAKAO, oauthId, "여행자1234", "https://example.com/new-profile.jpg");

        // then
        assertThat(result.isNewMember()).isFalse();
        assertThat(result.member()).isEqualTo(existingMember);
        assertThat(result.member().getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
        then(memberRepository).should(never()).save(any(Member.class));
    }
}
