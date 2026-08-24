package com.kk.klist.domain.chat.dto.response;

import com.kk.klist.domain.chat.dto.chatbot.ChatbotAudioQueryResponse;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotResponseStatus;
import java.util.List;

public record ChatAudioQueryResponse(
        String requestId,
        String sessionId,
        String traceId,
        ChatbotResponseStatus status,
        String transcription,
        String answer,
        List<String> suggestions
) {

    public static ChatAudioQueryResponse from(
            String requestId,
            String sessionId,
            String traceId,
            ChatbotAudioQueryResponse chatbotResponse
    ) {
        return new ChatAudioQueryResponse(
                requestId,
                sessionId,
                traceId,
                chatbotResponse.status(),
                chatbotResponse.transcription(),
                chatbotResponse.answer(),
                chatbotResponse.suggestions()
        );
    }
}
