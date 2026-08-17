package com.kk.klist.domain.chat.domain.exception;

import com.kk.klist.global.exception.BusinessException;

public class ChatException extends BusinessException {

    public ChatException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}
