package com.mcxx.chat.conversation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record TypingEvent(UUID conversationId, UUID userId, String fullName, boolean typing,
    Instant sentAt) {
}
