package com.kk.klist.global.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.kk.klist.global.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void 필수_쿼리파라미터가_누락되면_400을_반환한다() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("latitude", "double");

        ResponseEntity<ApiResponse<?>> response = handler.handleMissingParameterException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT.getCode());
    }
}
