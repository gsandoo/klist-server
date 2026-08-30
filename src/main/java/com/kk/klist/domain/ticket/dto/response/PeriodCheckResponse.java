package com.kk.klist.domain.ticket.dto.response;

public record PeriodCheckResponse(long completedCount) {

    public static PeriodCheckResponse of(long completedCount) {
        return new PeriodCheckResponse(completedCount);
    }
}
