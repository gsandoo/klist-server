package com.kk.klist.domain.chat.dto.chatbot;

public record ChatbotQueryResponse(
        ChatbotResponseStatus status,
        String answer
) {
}
