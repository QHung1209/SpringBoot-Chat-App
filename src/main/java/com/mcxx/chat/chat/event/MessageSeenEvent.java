package com.mcxx.chat.chat.event;

import java.util.UUID;

public record MessageSeenEvent(UUID conversationId, UUID messageId, UUID userId) {

}
