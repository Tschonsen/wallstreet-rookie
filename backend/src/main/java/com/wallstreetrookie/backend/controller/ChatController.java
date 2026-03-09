package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.request.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final Map<String, Instant> lastMessageTime = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_MS = 5000; // 5 Sekunden

    @MessageMapping("/chat.send")
    @SendTo("/topic/chat")
    public ChatMessage sendMessage(ChatMessage message) {
        // Rate Limiting
        Instant lastSent = lastMessageTime.get(message.username());
        if (lastSent != null && Instant.now().toEpochMilli() - lastSent.toEpochMilli() < RATE_LIMIT_MS) {
            log.warn("Rate limit für Chat-Nachricht von {}", message.username());
            return null;
        }

        lastMessageTime.put(message.username(), Instant.now());

        return new ChatMessage(
                message.username(),
                message.message(),
                Instant.now()
        );
    }
}
