package com.kk.klist.domain.chat.dto.chatbot;

import com.kk.klist.domain.chat.domain.ChatContextMessage;
import com.kk.klist.domain.chat.domain.ChatMessageRole;

public record ChatbotContextMessage(
        ChatMessageRole role,
        String content
) {

    public static ChatbotContextMessage from(ChatContextMessage message) {
        return new ChatbotContextMessage(message.role(), message.content());
    }
}
