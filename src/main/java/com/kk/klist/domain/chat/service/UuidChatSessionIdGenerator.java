package com.kk.klist.domain.chat.service;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidChatSessionIdGenerator implements ChatSessionIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
