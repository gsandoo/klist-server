package com.kk.klist.domain.chat.controller;

import com.kk.klist.domain.chat.dto.response.ChatSessionCreateResponse;
import com.kk.klist.domain.chat.dto.request.ChatQueryRequest;
import com.kk.klist.domain.chat.dto.response.ChatQueryResponse;
import com.kk.klist.domain.chat.service.ChatService;
import com.kk.klist.global.response.ApiResponse;
import com.kk.klist.global.security.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<ChatSessionCreateResponse>> createSession(@LoginUser Long userId) {
        ChatSessionCreateResponse response = chatService.createSession(userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<ChatQueryResponse>> query(
            @LoginUser Long userId,
            @Valid @RequestBody ChatQueryRequest request
    ) {
        ChatQueryResponse response = chatService.query(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
