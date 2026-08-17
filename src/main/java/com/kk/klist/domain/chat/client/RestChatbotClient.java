package com.kk.klist.domain.chat.client;

import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryRequest;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class RestChatbotClient implements ChatbotClient {

    private static final String QUERY_PATH = "/internal/chat/query";
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final RestClient chatbotRestClient;

    @Value("${chatbot.internal-api-key}")
    private String internalApiKey;

    @Override
    public ChatbotQueryResponse query(ChatbotQueryRequest request, String traceId) {
        try {
            ChatbotQueryResponse response = chatbotRestClient.post()
                    .uri(QUERY_PATH)
                    .header(INTERNAL_API_KEY_HEADER, internalApiKey)
                    .header(TRACE_ID_HEADER, traceId)
                    .body(request)
                    .retrieve()
                    .body(ChatbotQueryResponse.class);
            if (response == null || response.status() == null) {
                throw new ChatException(ChatErrorCode.CHATBOT_INVALID_RESPONSE);
            }
            return response;
        } catch (ChatException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ChatException(ChatErrorCode.CHATBOT_API_ERROR);
        }
    }
}
