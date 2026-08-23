package com.kk.klist.domain.chat.dto.chatbot;

import java.util.List;

public record ChatbotQueryResponse(
        ChatbotResponseStatus status,
        String answer,
        List<String> suggestions
) {
}
