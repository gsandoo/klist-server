package com.kk.klist.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.kk.klist.domain.chat.domain.ChatContextMessage;
import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.response.ChatSessionCreateResponse;
import com.kk.klist.domain.chat.repository.ChatSessionRepository;
import com.kk.klist.global.util.TimeProvider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Test
    @DisplayName("세션 소유자가 인증된 사용자와 같으면 검증에 성공한다")
    void validateSessionOwnership_whenOwnerMatches_succeeds() {
        // given
        String sessionId = "session-id";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(1L));

        // when
        chatService.validateSessionOwnership(1L, sessionId);

        // then
        then(chatSessionRepository).should(times(1)).findOwner(sessionId);
    }

    @Test
    @DisplayName("세션이 존재하지 않거나 만료되면 SessionNotFound 예외가 발생된다")
    void validateSessionOwnership_whenSessionMissing_throwsSessionNotFound() {
        // given
        String sessionId = "expired-session";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatService.validateSessionOwnership(1L, sessionId))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.SESSION_NOT_FOUND));
    }

    @Test
    @DisplayName("다른 사용자의 세션이면 SessionAccessDenied 예외가 발생된다")
    void validateSessionOwnership_whenOwnerDiffers_throwsSessionAccessDenied() {
        // given
        String sessionId = "other-session";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(2L));

        // when & then
        assertThatThrownBy(() -> chatService.validateSessionOwnership(1L, sessionId))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.SESSION_ACCESS_DENIED));
    }

    @Test
    @DisplayName("세션 소유자가 최근 문맥을 요청하면 최대 10개 문맥이 반환된다")
    void getRecentContext_whenOwnerMatches_returnsRecentContext() {
        // given
        String sessionId = "session-id";
        List<ChatContextMessage> context = List.of(
                ChatContextMessage.user("이전 질문"),
                ChatContextMessage.assistant("이전 답변")
        );
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(1L));
        given(chatSessionRepository.findRecentContext(sessionId, ChatService.CONTEXT_LIMIT))
                .willReturn(context);

        // when
        List<ChatContextMessage> result = chatService.getRecentContext(1L, sessionId);

        // then
        assertThat(result).containsExactlyElementsOf(context);
        then(chatSessionRepository).should(times(1))
                .findRecentContext(sessionId, ChatService.CONTEXT_LIMIT);
    }

    @Test
    @DisplayName("Chatbot 응답 완료 후 USER와 ASSISTANT 메시지 쌍을 순서대로 저장한다")
    void saveCompletedExchange_whenResponseCompleted_savesUserAssistantPair() {
        // given
        String sessionId = "session-id";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(1L));

        // when
        chatService.saveCompletedExchange(1L, sessionId, "현재 질문", "완료된 답변");

        // then
        then(chatSessionRepository).should(times(1)).saveCompletedExchange(
                sessionId,
                ChatContextMessage.user("현재 질문"),
                ChatContextMessage.assistant("완료된 답변"),
                ChatService.SESSION_TTL,
                ChatService.CONTEXT_LIMIT
        );
    }

    @Test
    @DisplayName("세션 소유권 검증에 실패하면 완료된 대화 쌍을 저장하지 않는다")
    void saveCompletedExchange_whenOwnershipInvalid_doesNotSaveContext() {
        // given
        String sessionId = "other-session";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(2L));

        // when & then
        assertThatThrownBy(() -> chatService.saveCompletedExchange(1L, sessionId, "질문", "답변"))
                .isInstanceOf(ChatException.class);
        then(chatSessionRepository).should(never()).saveCompletedExchange(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyInt()
        );
    }
}
