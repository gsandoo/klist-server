package com.kk.klist.domain.ticket.domain.entity;

import com.kk.klist.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int visitCount;

    @Builder
    private Ticket(Long memberId, LocalDate startDate, LocalDate endDate, int visitCount) {
        this.memberId = memberId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visitCount = visitCount;
    }

    public static Ticket create(Long memberId, LocalDate startDate, LocalDate endDate, int visitCount) {
        return Ticket.builder()
                .memberId(memberId)
                .startDate(startDate)
                .endDate(endDate)
                .visitCount(visitCount)
                .build();
    }
}
