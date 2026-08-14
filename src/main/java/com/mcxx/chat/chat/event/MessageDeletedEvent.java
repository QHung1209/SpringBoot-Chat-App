package com.mcxx.chat.chat.event;

import java.util.UUID;

public record MessageDeletedEvent(UUID conversationId, UUID messageId, UUID userId) {

}
