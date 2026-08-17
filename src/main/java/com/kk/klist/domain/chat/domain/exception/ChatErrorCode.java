package com.kk.klist.domain.chat.domain.exception;

import com.kk.klist.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    SESSION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT_SESSION_NOT_FOUND",
            "존재하지 않거나 만료된 채팅 세션입니다."
    ),
    SESSION_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "CHAT_SESSION_ACCESS_DENIED",
            "해당 채팅 세션에 접근할 권한이 없습니다."
    ),
    CHATBOT_API_ERROR(
            HttpStatus.BAD_GATEWAY,
            "CHATBOT_API_ERROR",
            "Chatbot 서버 요청에 실패했습니다."
    ),
    CHATBOT_INVALID_RESPONSE(
            HttpStatus.BAD_GATEWAY,
            "CHATBOT_INVALID_RESPONSE",
            "Chatbot 서버가 올바르지 않은 응답을 반환했습니다."
    ),
    SESSION_STORAGE_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "CHAT_SESSION_STORAGE_UNAVAILABLE",
            "채팅 세션 저장소를 사용할 수 없습니다. 잠시 후 다시 시도해주세요."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
