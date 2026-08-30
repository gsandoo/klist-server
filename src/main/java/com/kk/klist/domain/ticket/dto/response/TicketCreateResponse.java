package com.kk.klist.domain.ticket.dto.response;

import com.kk.klist.domain.ticket.domain.entity.BucketlistSnapshot;
import com.kk.klist.domain.ticket.domain.entity.Ticket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TicketCreateResponse(
        Long ticketId,
        LocalDate startDate,
        LocalDate endDate,
        int visitCount,
        LocalDateTime createdAt,
        List<BucketlistSnapshotItem> bucketlistSnapshot
) {

    public record BucketlistSnapshotItem(String title, LocalDateTime completedAt) {

        public static BucketlistSnapshotItem from(BucketlistSnapshot snapshot) {
            return new BucketlistSnapshotItem(snapshot.getTitle(), snapshot.getCompletedAt());
        }
    }

    public static TicketCreateResponse from(Ticket ticket, List<BucketlistSnapshot> snapshots) {
        return new TicketCreateResponse(
                ticket.getId(),
                ticket.getStartDate(),
                ticket.getEndDate(),
                ticket.getVisitCount(),
                ticket.getCreatedAt(),
                snapshots.stream().map(BucketlistSnapshotItem::from).toList()
        );
    }
}
