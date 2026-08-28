package com.kk.klist.domain.member.repository;

import com.kk.klist.domain.member.domain.entity.MemberWithdrawReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberWithdrawReasonRepositoryImpl implements MemberWithdrawReasonRepository {

    private final MemberWithdrawReasonJpaRepository jpaRepository;

    @Override
    public MemberWithdrawReason save(MemberWithdrawReason reason) {
        return jpaRepository.save(reason);
    }
}
