package com.kk.klist.domain.chat.repository;

import com.kk.klist.domain.chat.domain.ChatContextMessage;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository {

    void save(String sessionId, Long userId, Duration ttl);

    Optional<Long> findOwner(String sessionId);

    List<ChatContextMessage> findRecentContext(String sessionId, int limit);

    void saveCompletedExchange(
            String sessionId,
            ChatContextMessage userMessage,
            ChatContextMessage assistantMessage,
            Duration ttl,
            int contextLimit
    );
}
