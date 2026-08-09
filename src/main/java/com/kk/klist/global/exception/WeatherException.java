package com.kk.klist.global.exception;

public class WeatherException extends BusinessException {

    public WeatherException(WeatherErrorCode errorCode) {
        super(errorCode);
    }
}
