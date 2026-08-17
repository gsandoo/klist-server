package com.kk.klist.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.response.ChatSessionCreateResponse;
import com.kk.klist.domain.chat.repository.ChatSessionRepository;
import com.kk.klist.global.util.TimeProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatSessionIdGenerator sessionIdGenerator;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private ChatService chatService;

    @Test
    @DisplayName("채팅 세션을 생성하면 사용자 소유 관계가 3분 TTL로 저장되고 만료 시각이 반환된다")
    void createSession_whenAuthenticated_savesOwnershipAndReturnsExpiration() {
        // given
        Long userId = 1L;
        String sessionId = "6c92f36d-82cf-45ce-b0fe-61d097e60050";
        LocalDateTime now = LocalDateTime.of(2026, 8, 17, 12, 0);
        given(sessionIdGenerator.generate()).willReturn(sessionId);
        given(timeProvider.now()).willReturn(now);

        // when
        ChatSessionCreateResponse response = chatService.createSession(userId);

        // then
        assertThat(response.sessionId()).isEqualTo(sessionId);
        assertThat(response.expiresAt()).isEqualTo(now.plusMinutes(3));
        then(chatSessionRepository).should(times(1)).save(sessionId, userId, ChatService.SESSION_TTL);
    }

    @Test
    @DisplayName("Redis 저장에 실패하면 채팅 세션 생성 실패 예외가 전달된다")
    void createSession_whenStorageFails_throwsChatException() {
        // given
        Long userId = 1L;
        String sessionId = "6c92f36d-82cf-45ce-b0fe-61d097e60050";
        given(sessionIdGenerator.generate()).willReturn(sessionId);
        given(timeProvider.now()).willReturn(LocalDateTime.of(2026, 8, 17, 12, 0));
        org.mockito.BDDMockito.willThrow(new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE))
                .given(chatSessionRepository)
                .save(sessionId, userId, ChatService.SESSION_TTL);

        // when & then
        assertThatThrownBy(() -> chatService.createSession(userId))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE));
    }
}
