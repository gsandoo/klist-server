package com.kk.klist.domain.chat.service;

import com.kk.klist.domain.chat.domain.ChatContextMessage;
import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.response.ChatSessionCreateResponse;
import com.kk.klist.domain.chat.repository.ChatSessionRepository;
import com.kk.klist.global.util.TimeProvider;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    static final Duration SESSION_TTL = Duration.ofMinutes(3);
    static final int CONTEXT_LIMIT = 10;

    private final ChatSessionRepository chatSessionRepository;
    private final ChatSessionIdGenerator sessionIdGenerator;
    private final TimeProvider timeProvider;

    public ChatSessionCreateResponse createSession(Long userId) {
        String sessionId = sessionIdGenerator.generate();
        LocalDateTime expiresAt = timeProvider.now().plus(SESSION_TTL);

        chatSessionRepository.save(sessionId, userId, SESSION_TTL);

        return ChatSessionCreateResponse.of(sessionId, expiresAt);
    }

    public void validateSessionOwnership(Long userId, String sessionId) {
        Long ownerId = chatSessionRepository.findOwner(sessionId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.SESSION_NOT_FOUND));
        if (!ownerId.equals(userId)) {
            throw new ChatException(ChatErrorCode.SESSION_ACCESS_DENIED);
        }
    }

    public List<ChatContextMessage> getRecentContext(Long userId, String sessionId) {
        validateSessionOwnership(userId, sessionId);
        return chatSessionRepository.findRecentContext(sessionId, CONTEXT_LIMIT);
    }

    public void saveCompletedExchange(
            Long userId,
            String sessionId,
            String userMessage,
            String assistantMessage
    ) {
        validateSessionOwnership(userId, sessionId);
        chatSessionRepository.saveCompletedExchange(
                sessionId,
                ChatContextMessage.user(userMessage),
                ChatContextMessage.assistant(assistantMessage),
                SESSION_TTL,
                CONTEXT_LIMIT
        );
    }
}
