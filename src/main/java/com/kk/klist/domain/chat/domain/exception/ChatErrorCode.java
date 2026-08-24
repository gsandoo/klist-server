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
    CHATBOT_BAD_REQUEST(
            HttpStatus.BAD_REQUEST,
            "CHATBOT_BAD_REQUEST",
            "Chatbot 서버가 요청을 처리할 수 없습니다."
    ),
    CHATBOT_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "CHATBOT_UNAUTHORIZED",
            "Chatbot 서버 인증에 실패했습니다."
    ),
    CHATBOT_REQUEST_CONFLICT(
            HttpStatus.CONFLICT,
            "CHATBOT_REQUEST_CONFLICT",
            "동일한 Chatbot 요청이 이미 처리 중이거나 충돌했습니다."
    ),
    CHATBOT_INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "CHATBOT_INTERNAL_ERROR",
            "Chatbot 서버 내부 처리에 실패했습니다."
    ),
    CHATBOT_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "CHATBOT_UNAVAILABLE",
            "Chatbot 서버를 일시적으로 사용할 수 없습니다."
    ),
    CHATBOT_TIMEOUT(
            HttpStatus.GATEWAY_TIMEOUT,
            "CHATBOT_TIMEOUT",
            "Chatbot 서버 응답 시간이 초과되었습니다."
    ),
    CHATBOT_INVALID_RESPONSE(
            HttpStatus.BAD_GATEWAY,
            "CHATBOT_INVALID_RESPONSE",
            "Chatbot 서버가 올바르지 않은 응답을 반환했습니다."
    ),
    AUDIO_FILE_EMPTY(
            HttpStatus.BAD_REQUEST,
            "CHAT_AUDIO_FILE_EMPTY",
            "음성 파일은 필수입니다."
    ),
    STT_INVALID_RESPONSE(
            HttpStatus.BAD_GATEWAY,
            "STT_INVALID_RESPONSE",
            "음성을 텍스트로 변환하지 못했습니다."
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
