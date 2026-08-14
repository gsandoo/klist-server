package com.kk.klist.domain.bucketlist.domain.exception;

import com.kk.klist.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BucketListErrorCode implements ErrorCode {

    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "BUCKET_LIST_CATEGORY_NOT_FOUND", "지원하지 않는 카테고리입니다."),
    INCOMPLETE_COORDINATES(HttpStatus.BAD_REQUEST, "INCOMPLETE_COORDINATES", "위도와 경도를 모두 입력해주세요."),
    INVALID_COORDINATES(HttpStatus.BAD_REQUEST, "INVALID_COORDINATES", "위도 또는 경도의 범위가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
