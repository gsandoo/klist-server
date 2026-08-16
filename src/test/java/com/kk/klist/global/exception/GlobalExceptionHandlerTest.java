package com.kk.klist.global.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.kk.klist.global.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    @ParameterizedTest
    @EnumSource(AuthErrorCode.class)
    @DisplayName("AuthErrorCode로 예외가 발생하면 해당 코드의 HttpStatus/code/message로 ApiResponse가 직렬화된다")
    void handleBusinessException_whenAuthErrorCode_returnsMatchingApiResponse(AuthErrorCode errorCode) {
        // given
        AuthException exception = new AuthException(errorCode);

        // when
        ResponseEntity<ApiResponse<?>> response = handler.handleBusinessException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(errorCode.getHttpStatus());
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(errorCode.getMessage());
    }
}
