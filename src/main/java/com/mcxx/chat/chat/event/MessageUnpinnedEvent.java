package com.mcxx.chat.chat.event;

import java.util.UUID;

public record MessageUnpinnedEvent(UUID conversationId, UUID messageId, UUID userId) {

}
