package com.kk.klist.domain.chat.dto.response;

import java.time.LocalDateTime;

public record ChatSessionCreateResponse(
        String sessionId,
        LocalDateTime expiresAt
) {

    public static ChatSessionCreateResponse of(String sessionId, LocalDateTime expiresAt) {
        return new ChatSessionCreateResponse(sessionId, expiresAt);
    }
}
