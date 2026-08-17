package com.kk.klist.domain.chat.domain.exception;

import com.kk.klist.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    SESSION_STORAGE_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "CHAT_SESSION_STORAGE_UNAVAILABLE",
            "채팅 세션을 생성할 수 없습니다. 잠시 후 다시 시도해주세요."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
