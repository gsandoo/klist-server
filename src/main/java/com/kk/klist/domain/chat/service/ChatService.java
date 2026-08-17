package com.kk.klist.domain.chat.service;

import com.kk.klist.domain.chat.dto.response.ChatSessionCreateResponse;
import com.kk.klist.domain.chat.repository.ChatSessionRepository;
import com.kk.klist.global.util.TimeProvider;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    static final Duration SESSION_TTL = Duration.ofMinutes(3);

    private final ChatSessionRepository chatSessionRepository;
    private final ChatSessionIdGenerator sessionIdGenerator;
    private final TimeProvider timeProvider;

    public ChatSessionCreateResponse createSession(Long userId) {
        String sessionId = sessionIdGenerator.generate();
        LocalDateTime expiresAt = timeProvider.now().plus(SESSION_TTL);

        chatSessionRepository.save(sessionId, userId, SESSION_TTL);

        return ChatSessionCreateResponse.of(sessionId, expiresAt);
    }
}
