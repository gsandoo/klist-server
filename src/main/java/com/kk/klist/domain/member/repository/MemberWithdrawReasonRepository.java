package com.kk.klist.domain.member.repository;

import com.kk.klist.domain.member.domain.entity.MemberWithdrawReason;

public interface MemberWithdrawReasonRepository {
    MemberWithdrawReason save(MemberWithdrawReason reason);
}
