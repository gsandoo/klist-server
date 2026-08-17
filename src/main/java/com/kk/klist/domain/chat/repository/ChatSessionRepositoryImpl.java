package com.kk.klist.domain.chat.repository;

import com.kk.klist.domain.chat.domain.ChatContextMessage;
import com.kk.klist.domain.chat.domain.ChatMessageRole;
import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private static final String SESSION_KEY_PREFIX = "chat:session:";
    private static final String CONTEXT_KEY_PREFIX = "chat:context:";
    private static final String MESSAGE_SEPARATOR = ":";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String sessionId, Long userId, Duration ttl) {
        try {
            stringRedisTemplate.opsForValue().set(sessionKey(sessionId), userId.toString(), ttl);
        } catch (DataAccessException e) {
            throw new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE);
        }
    }

    @Override
    public Optional<Long> findOwner(String sessionId) {
        try {
            String ownerId = stringRedisTemplate.opsForValue().get(sessionKey(sessionId));
            return ownerId == null ? Optional.empty() : Optional.of(Long.valueOf(ownerId));
        } catch (DataAccessException | NumberFormatException e) {
            throw new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE);
        }
    }

    @Override
    public List<ChatContextMessage> findRecentContext(String sessionId, int limit) {
        try {
            List<String> messages = stringRedisTemplate.opsForList()
                    .range(contextKey(sessionId), -limit, -1);
            if (messages == null) {
                return List.of();
            }
            return messages.stream()
                    .map(this::deserialize)
                    .toList();
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE);
        }
    }

    @Override
    public void saveCompletedExchange(
            String sessionId,
            ChatContextMessage userMessage,
            ChatContextMessage assistantMessage,
            Duration ttl,
            int contextLimit
    ) {
        String sessionKey = sessionKey(sessionId);
        String contextKey = contextKey(sessionId);
        try {
            ListOperations<String, String> contextOperations = stringRedisTemplate.opsForList();
            Long contextSize = contextOperations.rightPushAll(
                    contextKey,
                    serialize(userMessage),
                    serialize(assistantMessage)
            );
            if (contextSize == null) {
                throw new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE);
            }
            contextOperations.trim(contextKey, -contextLimit, -1);

            Boolean sessionTtlUpdated = stringRedisTemplate.expire(sessionKey, ttl);
            Boolean contextTtlUpdated = stringRedisTemplate.expire(contextKey, ttl);
            if (!Boolean.TRUE.equals(sessionTtlUpdated) || !Boolean.TRUE.equals(contextTtlUpdated)) {
                throw new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE);
            }
        } catch (DataAccessException e) {
            throw new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE);
        }
    }

    private String sessionKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

    private String contextKey(String sessionId) {
        return CONTEXT_KEY_PREFIX + sessionId;
    }

    private String serialize(ChatContextMessage message) {
        String encodedContent = Base64.getEncoder()
                .encodeToString(message.content().getBytes(StandardCharsets.UTF_8));
        return message.role().name() + MESSAGE_SEPARATOR + encodedContent;
    }

    private ChatContextMessage deserialize(String value) {
        String[] parts = value.split(MESSAGE_SEPARATOR, 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid chat context message");
        }
        ChatMessageRole role = ChatMessageRole.valueOf(parts[0]);
        String content = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return new ChatContextMessage(role, content);
    }
}
