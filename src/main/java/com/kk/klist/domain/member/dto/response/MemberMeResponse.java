package com.kk.klist.domain.member.dto.response;

import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.global.security.auth.Role;

public record MemberMeResponse(
        Long id,
        String nickname,
        String nationality,
        String profileImageUrl,
        Role role) {

    public static MemberMeResponse from(Member member) {
        return new MemberMeResponse(
                member.getId(),
                member.getNickname(),
                member.getNationality(),
                member.getProfileImageUrl(),
                member.getRole());
    }
}
