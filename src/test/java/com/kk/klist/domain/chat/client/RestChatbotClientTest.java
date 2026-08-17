package com.kk.klist.domain.chat.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotContextMessage;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryRequest;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryResponse;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotResponseStatus;
import com.kk.klist.domain.chat.domain.ChatMessageRole;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RestChatbotClientTest {

    private MockRestServiceServer server;
    private RestChatbotClient chatbotClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://chatbot");
        server = MockRestServiceServer.bindTo(builder).build();
        chatbotClient = new RestChatbotClient(builder.build());
        ReflectionTestUtils.setField(chatbotClient, "internalApiKey", "internal-key");
    }

    @Test
    @DisplayName("Chatbot 내부 API에 인증 키와 trace ID 및 질문 요청을 전달한다")
    void query_whenChatbotResponds_returnsResponse() {
        // given
        ChatbotQueryRequest request = new ChatbotQueryRequest(
                "request-id",
                "session-id",
                1L,
                "현재 질문",
                List.of(new ChatbotContextMessage(ChatMessageRole.USER, "이전 질문")),
                5000L
        );
        server.expect(once(), requestTo("http://chatbot/internal/chat/query"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Api-Key", "internal-key"))
                .andExpect(header("X-Trace-Id", "trace-id"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"requestId\":\"request-id\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"timeoutMs\":5000")))
                .andRespond(withSuccess(
                        "{\"status\":\"COMPLETED\",\"answer\":\"완료된 답변\"}",
                        MediaType.APPLICATION_JSON
                ));

        // when
        ChatbotQueryResponse response = chatbotClient.query(request, "trace-id");

        // then
        assertThat(response.status()).isEqualTo(ChatbotResponseStatus.COMPLETED);
        assertThat(response.answer()).isEqualTo("완료된 답변");
        server.verify();
    }

    @Test
    @DisplayName("Chatbot 내부 API 호출에 실패하면 ChatbotApiError 예외가 발생한다")
    void query_whenChatbotFails_throwsChatbotApiError() {
        // given
        ChatbotQueryRequest request = new ChatbotQueryRequest(
                "request-id", "session-id", 1L, "질문", List.of(), 5000L);
        server.expect(once(), requestTo("http://chatbot/internal/chat/query"))
                .andRespond(withServerError());

        // when & then
        assertThatThrownBy(() -> chatbotClient.query(request, "trace-id"))
                .isInstanceOf(ChatException.class)
                .satisfies(error -> assertThat(((ChatException) error).getErrorCode())
                        .isEqualTo(ChatErrorCode.CHATBOT_API_ERROR));
    }
}
