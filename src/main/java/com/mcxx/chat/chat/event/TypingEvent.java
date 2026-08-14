package com.mcxx.chat.chat.event;

import java.time.Instant;
import java.util.UUID;

public record TypingEvent(
    UUID conversationId,
    UUID userId,
    String fullName,
    boolean typing,
    Instant sentAt
) {}
