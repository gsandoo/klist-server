package com.kk.klist.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.kk.klist.domain.chat.client.ChatbotClient;
import com.kk.klist.domain.chat.domain.ChatContextMessage;
import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.response.ChatSessionCreateResponse;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryRequest;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryResponse;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotResponseStatus;
import com.kk.klist.domain.chat.dto.request.ChatQueryRequest;
import com.kk.klist.domain.chat.dto.response.ChatQueryResponse;
import com.kk.klist.domain.chat.repository.ChatSessionRepository;
import com.kk.klist.global.util.TimeProvider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
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

    @Mock
    private ChatbotClient chatbotClient;

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

    @Test
    @DisplayName("Chatbot이 COMPLETED를 반환하면 응답을 변환하고 완료된 대화 쌍을 저장한다")
    void query_whenChatbotCompleted_returnsResponseAndSavesContext() {
        // given
        Long userId = 1L;
        String sessionId = "session-id";
        ChatQueryRequest request = new ChatQueryRequest(sessionId, "현재 질문");
        List<ChatContextMessage> context = List.of(
                ChatContextMessage.user("이전 질문"),
                ChatContextMessage.assistant("이전 답변")
        );
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(userId));
        given(sessionIdGenerator.generate()).willReturn("request-id", "trace-id");
        given(chatSessionRepository.findRecentContext(sessionId, ChatService.CONTEXT_LIMIT))
                .willReturn(context);
        given(chatbotClient.query(org.mockito.ArgumentMatchers.any(ChatbotQueryRequest.class),
                org.mockito.ArgumentMatchers.eq("trace-id")))
                .willReturn(new ChatbotQueryResponse(
                        ChatbotResponseStatus.COMPLETED,
                        "완료된 답변",
                        List.of("서울 실내 관광지를 추천해줘")
                ));

        // when
        ChatQueryResponse response = chatService.query(userId, request);

        // then
        assertThat(response.requestId()).isEqualTo("request-id");
        assertThat(response.traceId()).isEqualTo("trace-id");
        assertThat(response.status()).isEqualTo(ChatbotResponseStatus.COMPLETED);
        assertThat(response.answer()).isEqualTo("완료된 답변");
        assertThat(response.suggestions()).containsExactly("서울 실내 관광지를 추천해줘");

        ArgumentCaptor<ChatbotQueryRequest> requestCaptor = ArgumentCaptor.forClass(ChatbotQueryRequest.class);
        then(chatbotClient).should(times(1)).query(requestCaptor.capture(),
                org.mockito.ArgumentMatchers.eq("trace-id"));
        ChatbotQueryRequest chatbotRequest = requestCaptor.getValue();
        assertThat(chatbotRequest.requestId()).isEqualTo("request-id");
        assertThat(chatbotRequest.sessionId()).isEqualTo(sessionId);
        assertThat(chatbotRequest.userId()).isEqualTo(userId);
        assertThat(chatbotRequest.message()).isEqualTo("현재 질문");
        assertThat(chatbotRequest.timeoutMs()).isEqualTo(ChatService.CHATBOT_TIMEOUT_MS);
        assertThat(chatbotRequest.context()).extracting("content")
                .containsExactly("이전 질문", "이전 답변");
        then(chatSessionRepository).should(times(1)).saveCompletedExchange(
                sessionId,
                ChatContextMessage.user("현재 질문"),
                ChatContextMessage.assistant("완료된 답변"),
                ChatService.SESSION_TTL,
                ChatService.CONTEXT_LIMIT
        );
    }

    @ParameterizedTest
    @EnumSource(value = ChatbotResponseStatus.class, names = {
            "NO_RESULT", "UNSUPPORTED", "CLARIFICATION_REQUIRED"
    })
    @DisplayName("Chatbot이 완료 이외의 정상 상태를 반환하면 응답을 전달하고 문맥을 저장하지 않는다")
    void query_whenChatbotReturnsNonCompletedStatus_returnsResponseWithoutSavingContext(
            ChatbotResponseStatus status
    ) {
        // given
        String sessionId = "session-id";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(1L));
        given(sessionIdGenerator.generate()).willReturn("request-id", "trace-id");
        given(chatSessionRepository.findRecentContext(sessionId, ChatService.CONTEXT_LIMIT))
                .willReturn(List.of());
        given(chatbotClient.query(org.mockito.ArgumentMatchers.any(ChatbotQueryRequest.class),
                org.mockito.ArgumentMatchers.eq("trace-id")))
                .willReturn(new ChatbotQueryResponse(
                        status,
                        "조건에 맞는 답변입니다.",
                        List.of("지역을 알려주세요")
                ));

        // when
        ChatQueryResponse response = chatService.query(1L, new ChatQueryRequest(sessionId, "현재 질문"));

        // then
        assertThat(response.status()).isEqualTo(status);
        assertThat(response.answer()).isEqualTo("조건에 맞는 답변입니다.");
        assertThat(response.suggestions()).containsExactly("지역을 알려주세요");
        then(chatSessionRepository).should(never()).saveCompletedExchange(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyInt()
        );
    }

    @Test
    @DisplayName("Chatbot 정상 응답의 answer가 비어 있으면 잘못된 응답 예외가 발생된다")
    void query_whenChatbotAnswerIsBlank_throwsInvalidResponse() {
        // given
        String sessionId = "session-id";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(1L));
        given(sessionIdGenerator.generate()).willReturn("request-id", "trace-id");
        given(chatSessionRepository.findRecentContext(sessionId, ChatService.CONTEXT_LIMIT))
                .willReturn(List.of());
        given(chatbotClient.query(org.mockito.ArgumentMatchers.any(ChatbotQueryRequest.class),
                org.mockito.ArgumentMatchers.eq("trace-id")))
                .willReturn(new ChatbotQueryResponse(
                        ChatbotResponseStatus.NO_RESULT,
                        " ",
                        List.of("다른 지역을 검색해줘")
                ));

        // when & then
        assertThatThrownBy(() -> chatService.query(1L, new ChatQueryRequest(sessionId, "현재 질문")))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.CHATBOT_INVALID_RESPONSE));
    }

    @Test
    @DisplayName("Chatbot 정상 응답의 suggestions가 비어 있으면 잘못된 응답 예외가 발생된다")
    void query_whenChatbotSuggestionsAreEmpty_throwsInvalidResponse() {
        // given
        String sessionId = "session-id";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(1L));
        given(sessionIdGenerator.generate()).willReturn("request-id", "trace-id");
        given(chatSessionRepository.findRecentContext(sessionId, ChatService.CONTEXT_LIMIT))
                .willReturn(List.of());
        given(chatbotClient.query(org.mockito.ArgumentMatchers.any(ChatbotQueryRequest.class),
                org.mockito.ArgumentMatchers.eq("trace-id")))
                .willReturn(new ChatbotQueryResponse(
                        ChatbotResponseStatus.UNSUPPORTED,
                        "관광 관련 질문을 해주세요.",
                        List.of()
                ));

        // when & then
        assertThatThrownBy(() -> chatService.query(1L, new ChatQueryRequest(sessionId, "현재 질문")))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.CHATBOT_INVALID_RESPONSE));
    }

    @Test
    @DisplayName("Chatbot 호출이 실패하면 문맥을 저장하지 않는다")
    void query_whenChatbotFails_doesNotSaveContext() {
        // given
        String sessionId = "session-id";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(1L));
        given(sessionIdGenerator.generate()).willReturn("request-id", "trace-id");
        given(chatSessionRepository.findRecentContext(sessionId, ChatService.CONTEXT_LIMIT))
                .willReturn(List.of());
        given(chatbotClient.query(org.mockito.ArgumentMatchers.any(ChatbotQueryRequest.class),
                org.mockito.ArgumentMatchers.eq("trace-id")))
                .willThrow(new ChatException(ChatErrorCode.CHATBOT_API_ERROR));

        // when & then
        assertThatThrownBy(() -> chatService.query(1L, new ChatQueryRequest(sessionId, "현재 질문")))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.CHATBOT_API_ERROR));
        then(chatSessionRepository).should(never()).saveCompletedExchange(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyInt()
        );
    }

    @Test
    @DisplayName("세션 소유권 검증에 실패하면 문맥 조회와 Chatbot 호출을 하지 않는다")
    void query_whenOwnershipInvalid_doesNotCallChatbot() {
        // given
        String sessionId = "other-session";
        given(chatSessionRepository.findOwner(sessionId)).willReturn(Optional.of(2L));

        // when & then
        assertThatThrownBy(() -> chatService.query(1L, new ChatQueryRequest(sessionId, "질문")))
                .isInstanceOf(ChatException.class);
        then(chatSessionRepository).should(never()).findRecentContext(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyInt());
        then(chatbotClient).shouldHaveNoInteractions();
    }
}
