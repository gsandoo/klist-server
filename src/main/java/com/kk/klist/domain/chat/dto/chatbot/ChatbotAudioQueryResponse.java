package com.kk.klist.domain.chat.dto.chatbot;

import java.util.List;

public record ChatbotAudioQueryResponse(
        ChatbotResponseStatus status,
        String transcription,
        String answer,
        List<String> suggestions
) {
}
