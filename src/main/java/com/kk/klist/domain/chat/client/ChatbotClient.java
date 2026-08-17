package com.kk.klist.domain.chat.client;

import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryRequest;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryResponse;

public interface ChatbotClient {

    ChatbotQueryResponse query(ChatbotQueryRequest request, String traceId);
}
