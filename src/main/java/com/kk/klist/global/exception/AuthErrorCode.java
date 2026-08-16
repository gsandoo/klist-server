package com.kk.klist.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "권한이 없습니다."),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "A003", "지원하지 않는 로그인 제공자입니다."),
    OAUTH_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A004", "소셜 로그인 인증에 실패했습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 Refresh Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "Refresh Token이 만료되었습니다. 다시 로그인해주세요."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A007", "Access Token이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
