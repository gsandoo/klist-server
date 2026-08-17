package com.kk.klist.domain.chat.repository;

import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private static final String KEY_PREFIX = "chat:session:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String sessionId, Long userId, Duration ttl) {
        try {
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + sessionId, userId.toString(), ttl);
        } catch (DataAccessException e) {
            throw new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE);
        }
    }
}
