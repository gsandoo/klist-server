package com.kk.klist.domain.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.kk.klist.domain.chat.domain.ChatContextMessage;
import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class ChatSessionRepositoryImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ListOperations<String, String> listOperations;

    private ChatSessionRepositoryImpl chatSessionRepository;

    @BeforeEach
    void setUp() {
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
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

    @Test
    @DisplayName("세션 키가 존재하면 저장된 사용자 ID를 소유자로 반환한다")
    void findOwner_whenSessionExists_returnsOwnerId() {
        // given
        given(valueOperations.get("chat:session:session-id")).willReturn("21");

        // when
        Optional<Long> ownerId = chatSessionRepository.findOwner("session-id");

        // then
        assertThat(ownerId).contains(21L);
    }

    @Test
    @DisplayName("세션 키가 존재하지 않으면 빈 소유자를 반환한다")
    void findOwner_whenSessionMissing_returnsEmpty() {
        // given
        given(valueOperations.get("chat:session:expired-session")).willReturn(null);

        // when
        Optional<Long> ownerId = chatSessionRepository.findOwner("expired-session");

        // then
        assertThat(ownerId).isEmpty();
    }

    @Test
    @DisplayName("최근 문맥을 조회하면 마지막 10개를 오래된 순서로 역직렬화한다")
    void findRecentContext_whenMessagesExist_returnsLastTenInStoredOrder() {
        // given
        given(listOperations.range("chat:context:session-id", -10, -1))
                .willReturn(List.of(
                        serialized("USER", "이전 질문"),
                        serialized("ASSISTANT", "이전 답변")
                ));

        // when
        List<ChatContextMessage> context = chatSessionRepository.findRecentContext("session-id", 10);

        // then
        assertThat(context).containsExactly(
                ChatContextMessage.user("이전 질문"),
                ChatContextMessage.assistant("이전 답변")
        );
    }

    @Test
    @DisplayName("완료된 대화 쌍을 USER ASSISTANT 순서로 저장한 후 문맥을 제한하고 TTL을 갱신한다")
    void saveCompletedExchange_whenRedisAvailable_savesPairThenRefreshesTtl() {
        // given
        String sessionId = "session-id";
        Duration ttl = Duration.ofMinutes(3);
        given(listOperations.rightPushAll(
                "chat:context:" + sessionId,
                serialized("USER", "현재 질문"),
                serialized("ASSISTANT", "완료된 답변")
        )).willReturn(2L);
        given(stringRedisTemplate.expire("chat:session:" + sessionId, ttl)).willReturn(true);
        given(stringRedisTemplate.expire("chat:context:" + sessionId, ttl)).willReturn(true);

        // when
        chatSessionRepository.saveCompletedExchange(
                sessionId,
                ChatContextMessage.user("현재 질문"),
                ChatContextMessage.assistant("완료된 답변"),
                ttl,
                10
        );

        // then
        InOrder inOrder = inOrder(listOperations, stringRedisTemplate);
        inOrder.verify(listOperations).rightPushAll(
                "chat:context:" + sessionId,
                serialized("USER", "현재 질문"),
                serialized("ASSISTANT", "완료된 답변")
        );
        inOrder.verify(listOperations).trim("chat:context:" + sessionId, -10, -1);
        inOrder.verify(stringRedisTemplate).expire("chat:session:" + sessionId, ttl);
        inOrder.verify(stringRedisTemplate).expire("chat:context:" + sessionId, ttl);
    }

    private String serialized(String role, String content) {
        return role + ":" + Base64.getEncoder()
                .encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }
}
