package com.kk.klist.domain.chat.client;

import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryRequest;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotQueryResponse;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotAudioQueryRequest;
import com.kk.klist.domain.chat.dto.chatbot.ChatbotAudioQueryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ChatbotClient {

    ChatbotQueryResponse query(ChatbotQueryRequest request, String traceId);

    ChatbotAudioQueryResponse queryAudio(
            ChatbotAudioQueryRequest request,
            MultipartFile audio,
            String traceId
    );
}
