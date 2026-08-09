package com.kk.klist.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WeatherErrorCode implements ErrorCode {

    INVALID_COORDINATES(HttpStatus.BAD_REQUEST, "W001", "위도/경도 값이 올바르지 않습니다."),
    WEATHER_API_ERROR(HttpStatus.BAD_GATEWAY, "W002", "날씨 정보를 불러오지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
