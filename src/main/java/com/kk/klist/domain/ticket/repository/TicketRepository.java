package com.kk.klist.domain.ticket.repository;

import com.kk.klist.domain.ticket.domain.entity.Ticket;
import java.time.LocalDate;
import java.util.List;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    boolean existsOverlappingTicket(Long memberId, LocalDate startDate, LocalDate endDate);

    List<Ticket> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    long countByMemberId(Long memberId);
}
