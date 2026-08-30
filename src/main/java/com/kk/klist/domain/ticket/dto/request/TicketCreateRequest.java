package com.kk.klist.domain.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TicketCreateRequest(

        @NotNull(message = "시작일을 입력해주세요.")
        LocalDate startDate,

        @NotNull(message = "종료일을 입력해주세요.")
        LocalDate endDate
) {
}
