package com.kk.klist.domain.member.domain.entity;

import com.kk.klist.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_withdraw_reasons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWithdrawReason extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    private MemberWithdrawReason(Long memberId, String reason) {
        this.memberId = memberId;
        this.reason = reason;
    }

    public static MemberWithdrawReason of(Long memberId, String reason) {
        return new MemberWithdrawReason(memberId, reason);
    }
}
