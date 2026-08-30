package com.kk.klist.domain.ticket.domain.exception;

import com.kk.klist.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TicketErrorCode implements ErrorCode {

    TICKET_DATE_OVERLAP(HttpStatus.CONFLICT, "TICKET_DATE_OVERLAP", "이미 기록된 여행 기간과 겹칩니다. 기간을 다시 선택해주세요."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "INVALID_DATE_RANGE", "종료일은 시작일보다 늦어야 합니다."),
    FUTURE_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FUTURE_DATE_NOT_ALLOWED", "시작일과 종료일은 오늘 이전이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
