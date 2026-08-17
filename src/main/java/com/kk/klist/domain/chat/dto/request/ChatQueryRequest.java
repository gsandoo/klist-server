package com.kk.klist.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatQueryRequest(
        @NotBlank(message = "세션 ID는 필수입니다.")
        String sessionId,

        @NotBlank(message = "질문은 필수입니다.")
        @Size(max = 4000, message = "질문은 4000자 이하여야 합니다.")
        String message
) {
}
