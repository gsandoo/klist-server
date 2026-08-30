package com.kk.klist.domain.ticket.repository;

import com.kk.klist.domain.ticket.domain.entity.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketJpaRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    long countByMemberId(Long memberId);
}
