package com.kk.klist.domain.chat.dto.response;

import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryResponse;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotResponseStatus;

public record ChatQueryResponse(
        String requestId,
        String sessionId,
        String traceId,
        ChatbotResponseStatus status,
        String answer
) {

    public static ChatQueryResponse from(
            String requestId,
            String sessionId,
            String traceId,
            ChatbotQueryResponse chatbotResponse
    ) {
        return new ChatQueryResponse(
                requestId,
                sessionId,
                traceId,
                chatbotResponse.status(),
                chatbotResponse.answer()
        );
    }
}
