package com.kk.klist.domain.ticket.service;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import com.kk.klist.domain.bucketlist.service.BucketListService;
import com.kk.klist.domain.ticket.domain.entity.BucketlistSnapshot;
import com.kk.klist.domain.ticket.domain.entity.Ticket;
import com.kk.klist.domain.ticket.domain.exception.TicketErrorCode;
import com.kk.klist.domain.ticket.domain.exception.TicketException;
import com.kk.klist.domain.ticket.dto.request.TicketCreateRequest;
import com.kk.klist.domain.ticket.dto.response.PeriodCheckResponse;
import com.kk.klist.domain.ticket.dto.response.TicketCreateResponse;
import com.kk.klist.domain.ticket.dto.response.TicketSummaryResponse;
import com.kk.klist.domain.ticket.repository.BucketlistSnapshotRepository;
import com.kk.klist.domain.ticket.repository.TicketRepository;
import com.kk.klist.global.util.TimeProvider;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BucketlistSnapshotRepository bucketlistSnapshotRepository;
    private final BucketListService bucketListService;
    private final TimeProvider timeProvider;

    @Transactional
    public TicketCreateResponse createTicket(Long memberId, TicketCreateRequest request) {
        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();
        LocalDate today = timeProvider.now().toLocalDate();

        if (startDate.isAfter(endDate)) {
            throw new TicketException(TicketErrorCode.INVALID_DATE_RANGE);
        }
        if (endDate.isAfter(today)) {
            throw new TicketException(TicketErrorCode.FUTURE_DATE_NOT_ALLOWED);
        }
        if (ticketRepository.existsOverlappingTicket(memberId, startDate, endDate)) {
            throw new TicketException(TicketErrorCode.TICKET_DATE_OVERLAP);
        }

        List<BucketList> completedBucketLists =
                bucketListService.findAllCompletedInPeriod(memberId, startDate, endDate);
        int visitCount = (int) ticketRepository.countByMemberId(memberId) + 1;

        Ticket ticket = ticketRepository.save(Ticket.create(memberId, startDate, endDate, visitCount));

        List<BucketlistSnapshot> snapshots = completedBucketLists.stream()
                .map(bl -> BucketlistSnapshot.create(ticket.getId(), bl.getId(), bl.getTitle(),
                        bl.getCompletedAt(), bl.getCategory().getCode()))
                .toList();
        bucketlistSnapshotRepository.saveAll(snapshots);

        return TicketCreateResponse.from(ticket, snapshots);
    }

    public List<TicketSummaryResponse> findTickets(Long memberId) {
        return ticketRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId).stream()
                .map(ticket -> TicketSummaryResponse.from(
                        ticket,
                        bucketlistSnapshotRepository.findAllByTicketId(ticket.getId())))
                .toList();
    }

    public PeriodCheckResponse checkPeriod(Long memberId, LocalDate startDate, LocalDate endDate) {
        long count = bucketListService.countCompletedInPeriod(memberId, startDate, endDate);
        return PeriodCheckResponse.of(count);
    }
}
