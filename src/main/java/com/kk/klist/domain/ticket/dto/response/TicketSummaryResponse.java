package com.kk.klist.domain.ticket.dto.response;

import com.kk.klist.domain.ticket.domain.entity.Ticket;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TicketSummaryResponse(
        Long ticketId,
        LocalDate startDate,
        LocalDate endDate,
        int visitCount,
        LocalDateTime createdAt
) {

    public static TicketSummaryResponse from(Ticket ticket) {
        return new TicketSummaryResponse(
                ticket.getId(),
                ticket.getStartDate(),
                ticket.getEndDate(),
                ticket.getVisitCount(),
                ticket.getCreatedAt()
        );
    }
}
