package com.kk.klist.domain.chat.client;

import com.kk.klist.domain.chat.domain.exception.ChatErrorCode;
import com.kk.klist.domain.chat.domain.exception.ChatException;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryRequest;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryResponse;
import java.io.InterruptedIOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestChatbotClient implements ChatbotClient {

    private static final String QUERY_PATH = "/internal/chat/query";
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final RestClient chatbotRestClient;
    private final String internalApiKey;

    public RestChatbotClient(
            RestClient chatbotRestClient,
            @Value("${chatbot.internal-api-key}") String internalApiKey
    ) {
        this.chatbotRestClient = chatbotRestClient;
        this.internalApiKey = internalApiKey;
    }

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
        } catch (HttpStatusCodeException e) {
            throw new ChatException(mapStatus(e.getStatusCode()));
        } catch (ResourceAccessException e) {
            if (hasSocketTimeoutCause(e)) {
                throw new ChatException(ChatErrorCode.CHATBOT_TIMEOUT);
            }
            throw new ChatException(ChatErrorCode.CHATBOT_API_ERROR);
        } catch (RestClientException e) {
            if (hasSocketTimeoutCause(e)) {
                throw new ChatException(ChatErrorCode.CHATBOT_TIMEOUT);
            }
            throw new ChatException(ChatErrorCode.CHATBOT_API_ERROR);
        }
    }

    private ChatErrorCode mapStatus(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> ChatErrorCode.CHATBOT_BAD_REQUEST;
            case 401 -> ChatErrorCode.CHATBOT_UNAUTHORIZED;
            case 409 -> ChatErrorCode.CHATBOT_REQUEST_CONFLICT;
            case 500 -> ChatErrorCode.CHATBOT_INTERNAL_ERROR;
            case 503 -> ChatErrorCode.CHATBOT_UNAVAILABLE;
            case 504 -> ChatErrorCode.CHATBOT_TIMEOUT;
            default -> ChatErrorCode.CHATBOT_API_ERROR;
        };
    }

    private boolean hasSocketTimeoutCause(Throwable error) {
        Throwable cause = error;
        while (cause != null) {
            if (cause instanceof InterruptedIOException
                    || cause.getClass().getSimpleName().contains("Timeout")
                    || cause.getMessage() != null && cause.getMessage().toLowerCase().contains("timed out")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
