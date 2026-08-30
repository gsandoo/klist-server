package com.kk.klist.domain.ticket.domain.exception;

import com.kk.klist.global.exception.BusinessException;

public class TicketException extends BusinessException {

    public TicketException(TicketErrorCode errorCode) {
        super(errorCode);
    }
}
