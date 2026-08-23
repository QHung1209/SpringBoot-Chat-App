package com.mcxx.chat.chat.event;

import java.util.List;
import java.util.UUID;

public record CreateGroupConversation(UUID conversationId, UUID userId, List<UUID> memberIds) {

}
