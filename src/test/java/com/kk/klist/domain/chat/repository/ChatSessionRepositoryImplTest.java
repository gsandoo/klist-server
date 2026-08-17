package com.kk.klist.domain.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class ChatSessionRepositoryImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private ChatSessionRepositoryImpl chatSessionRepository;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        chatSessionRepository = new ChatSessionRepositoryImpl(stringRedisTemplate);
    }

    @Test
    @DisplayName("채팅 세션을 저장하면 세션 키와 사용자 ID에 3분 TTL이 적용된다")
    void save_whenRedisAvailable_storesOwnershipWithTtl() {
        // given
        String sessionId = "6c92f36d-82cf-45ce-b0fe-61d097e60050";
        Duration ttl = Duration.ofMinutes(3);

        // when
        chatSessionRepository.save(sessionId, 1L, ttl);

        // then
        then(valueOperations).should(times(1)).set("chat:session:" + sessionId, "1", ttl);
    }

    @Test
    @DisplayName("Redis 저장에 실패하면 채팅 세션 생성 실패 예외가 발생된다")
    void save_whenRedisFails_throwsChatException() {
        // given
        String sessionId = "6c92f36d-82cf-45ce-b0fe-61d097e60050";
        Duration ttl = Duration.ofMinutes(3);
        willThrow(new DataAccessResourceFailureException("Redis unavailable"))
                .given(valueOperations)
                .set("chat:session:" + sessionId, "1", ttl);

        // when & then
        assertThatThrownBy(() -> chatSessionRepository.save(sessionId, 1L, ttl))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE));
    }
}
