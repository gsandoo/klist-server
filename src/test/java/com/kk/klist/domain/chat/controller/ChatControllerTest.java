package com.kk.klist.domain.chat.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.response.ChatSessionCreateResponse;
import com.kk.klist.domain.chat.dto.request.ChatQueryRequest;
import com.kk.klist.domain.chat.dto.response.ChatQueryResponse;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotResponseStatus;
import com.kk.klist.domain.chat.service.ChatService;
import com.kk.klist.global.security.config.SecurityConfig;
import com.kk.klist.global.security.auth.CustomUserDetails;
import com.kk.klist.global.security.auth.Role;
import com.kk.klist.global.security.jwt.JwtTokenProvider;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
@Import(SecurityConfig.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /api/v1/chat/sessions 요청이 인증되면 201과 세션 정보가 반환된다")
    void createSession_whenAuthenticated_returns201WithSession() throws Exception {
        // given
        Long userId = 1L;
        String sessionId = "6c92f36d-82cf-45ce-b0fe-61d097e60050";
        LocalDateTime expiresAt = LocalDateTime.of(2026, 8, 17, 12, 3);
        given(chatService.createSession(userId))
                .willReturn(ChatSessionCreateResponse.of(sessionId, expiresAt));

        // when & then
        mockMvc.perform(post("/api/v1/chat/sessions")
                        .with(authentication(createAuthentication(userId)))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionId").value(sessionId))
                .andExpect(jsonPath("$.data.expiresAt").value("2026-08-17T12:03:00"));
        then(chatService).should(times(1)).createSession(userId);
    }

    @Test
    @DisplayName("POST /api/v1/chat/sessions 요청이 인증되지 않으면 401이 반환된다")
    void createSession_whenUnauthenticated_returns401() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/chat/sessions").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
        then(chatService).should(never()).createSession(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("POST /api/v1/chat/sessions Redis 저장이 실패하면 503이 반환된다")
    void createSession_whenStorageFails_returns503() throws Exception {
        // given
        Long userId = 1L;
        given(chatService.createSession(userId))
                .willThrow(new ChatException(ChatErrorCode.SESSION_STORAGE_UNAVAILABLE));

        // when & then
        mockMvc.perform(post("/api/v1/chat/sessions")
                        .with(authentication(createAuthentication(userId)))
                        .with(csrf()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("CHAT_SESSION_STORAGE_UNAVAILABLE"));
    }

    @ParameterizedTest
    @EnumSource(ChatbotResponseStatus.class)
    @DisplayName("POST /api/v1/chat/query가 각 Chatbot 정상 상태와 답변 및 후속 질문을 200으로 반환한다")
    void query_whenChatbotReturnsNormalStatus_returns200WithResponse(
            ChatbotResponseStatus chatbotStatus
    ) throws Exception {
        // given
        Long userId = 1L;
        ChatQueryResponse response = new ChatQueryResponse(
                "request-id",
                "session-id",
                "trace-id",
                chatbotStatus,
                "사용자용 답변",
                List.of("서울 관광지를 추천해줘", "부산 관광지를 추천해줘")
        );
        given(chatService.query(org.mockito.ArgumentMatchers.eq(userId),
                org.mockito.ArgumentMatchers.any(ChatQueryRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/chat/query")
                        .with(authentication(createAuthentication(userId)))
                        .with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sessionId": "session-id",
                                  "message": "현재 질문"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requestId").value("request-id"))
                .andExpect(jsonPath("$.data.sessionId").value("session-id"))
                .andExpect(jsonPath("$.data.traceId").value("trace-id"))
                .andExpect(jsonPath("$.data.status").value(chatbotStatus.name()))
                .andExpect(jsonPath("$.data.answer").value("사용자용 답변"))
                .andExpect(jsonPath("$.data.suggestions[0]").value("서울 관광지를 추천해줘"))
                .andExpect(jsonPath("$.data.suggestions[1]").value("부산 관광지를 추천해줘"));
    }

    @Test
    @DisplayName("POST /api/v1/chat/query 질문이 공백이면 400을 반환한다")
    void query_whenMessageBlank_returns400() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/chat/query")
                        .with(authentication(createAuthentication(1L)))
                        .with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sessionId": "session-id",
                                  "message": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("G002"))
                .andExpect(jsonPath("$.errors[0].field").value("message"));
        then(chatService).should(never()).query(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("POST /api/v1/chat/query 질문이 4000자를 초과하면 400을 반환한다")
    void query_whenMessageTooLong_returns400() throws Exception {
        // given
        String message = "a".repeat(4001);

        // when & then
        mockMvc.perform(post("/api/v1/chat/query")
                        .with(authentication(createAuthentication(1L)))
                        .with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"session-id\",\"message\":\"" + message + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("G002"));
    }

    private UsernamePasswordAuthenticationToken createAuthentication(Long userId) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, Role.USER);
        return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
    }
}
