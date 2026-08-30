package com.kk.klist.domain.ticket.repository;

import static com.kk.klist.domain.ticket.domain.entity.QTicket.ticket;

import com.kk.klist.domain.ticket.domain.entity.Ticket;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Ticket save(Ticket ticketEntity) {
        return ticketJpaRepository.save(ticketEntity);
    }

    @Override
    public boolean existsOverlappingTicket(Long memberId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectOne()
                .from(ticket)
                .where(
                        memberIdEq(memberId),
                        startDateLoe(endDate),
                        endDateGoe(startDate)
                )
                .limit(1)
                .fetchFirst() != null;
    }

    @Override
    public List<Ticket> findAllByMemberIdOrderByCreatedAtDesc(Long memberId) {
        return ticketJpaRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Override
    public long countByMemberId(Long memberId) {
        return ticketJpaRepository.countByMemberId(memberId);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return ticket.memberId.eq(memberId);
    }

    private BooleanExpression startDateLoe(LocalDate endDate) {
        return ticket.startDate.loe(endDate);
    }

    private BooleanExpression endDateGoe(LocalDate startDate) {
        return ticket.endDate.goe(startDate);
    }
}
