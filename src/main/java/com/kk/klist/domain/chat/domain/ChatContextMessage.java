package com.kk.klist.domain.chat.domain;

public record ChatContextMessage(
        ChatMessageRole role,
        String content
) {

    public static ChatContextMessage user(String content) {
        return new ChatContextMessage(ChatMessageRole.USER, content);
    }

    public static ChatContextMessage assistant(String content) {
        return new ChatContextMessage(ChatMessageRole.ASSISTANT, content);
    }
}
