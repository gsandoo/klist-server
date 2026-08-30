package com.kk.klist.domain.ticket.controller;

import com.kk.klist.domain.ticket.dto.request.TicketCreateRequest;
import com.kk.klist.domain.ticket.dto.response.PeriodCheckResponse;
import com.kk.klist.domain.ticket.dto.response.TicketCreateResponse;
import com.kk.klist.domain.ticket.dto.response.TicketSummaryResponse;
import com.kk.klist.domain.ticket.service.TicketService;
import com.kk.klist.global.response.ApiResponse;
import com.kk.klist.global.security.auth.LoginUser;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketCreateResponse>> createTicket(
            @LoginUser Long userId,
            @Valid @RequestBody TicketCreateRequest request
    ) {
        TicketCreateResponse response = ticketService.createTicket(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketSummaryResponse>>> findTickets(
            @LoginUser Long userId
    ) {
        List<TicketSummaryResponse> response = ticketService.findTickets(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/period-check")
    public ResponseEntity<ApiResponse<PeriodCheckResponse>> checkPeriod(
            @LoginUser Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        PeriodCheckResponse response = ticketService.checkPeriod(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
