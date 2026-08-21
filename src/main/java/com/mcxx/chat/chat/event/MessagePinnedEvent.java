package com.mcxx.chat.chat.event;

import java.util.UUID;

public record MessagePinnedEvent(UUID conversationId, UUID messageId, UUID userId) {

}
