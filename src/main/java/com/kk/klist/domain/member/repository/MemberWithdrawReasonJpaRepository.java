package com.kk.klist.domain.member.repository;

import com.kk.klist.domain.member.domain.entity.MemberWithdrawReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWithdrawReasonJpaRepository extends JpaRepository<MemberWithdrawReason, Long> {
}
