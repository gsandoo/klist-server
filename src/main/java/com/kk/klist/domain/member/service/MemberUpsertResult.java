package com.kk.klist.domain.member.service;

import com.kk.klist.domain.member.domain.entity.Member;

public record MemberUpsertResult(Member member, boolean isNewMember) {
}
