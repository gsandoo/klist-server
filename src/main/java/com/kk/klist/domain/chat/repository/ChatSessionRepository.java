package com.kk.klist.domain.chat.repository;

import java.time.Duration;

public interface ChatSessionRepository {

    void save(String sessionId, Long userId, Duration ttl);
}
