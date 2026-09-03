package com.kk.klist.domain.member.domain.exception;

import com.kk.klist.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    ONBOARDING_ALREADY_COMPLETED(HttpStatus.CONFLICT, "M001", "이미 온보딩이 완료된 사용자입니다."),
    KAKAO_UNLINK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M002", "탈퇴 실패, 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
