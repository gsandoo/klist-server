package com.kk.klist.domain.ticket.dto.response;

import com.kk.klist.domain.ticket.domain.entity.BucketlistSnapshot;
import com.kk.klist.domain.ticket.domain.entity.Ticket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TicketSummaryResponse(
        Long ticketId,
        LocalDate startDate,
        LocalDate endDate,
        int visitCount,
        LocalDateTime createdAt,
        int completedCount,
        List<String> categories
) {

    public static TicketSummaryResponse from(Ticket ticket, List<BucketlistSnapshot> snapshots) {
        List<String> categories = snapshots.stream()
                .map(BucketlistSnapshot::getCategoryCode)
                .distinct()
                .toList();
        return new TicketSummaryResponse(
                ticket.getId(),
                ticket.getStartDate(),
                ticket.getEndDate(),
                ticket.getVisitCount(),
                ticket.getCreatedAt(),
                snapshots.size(),
                categories
        );
    }
}
