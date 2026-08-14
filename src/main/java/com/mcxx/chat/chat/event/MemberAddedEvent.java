package com.mcxx.chat.chat.event;

import java.util.UUID;

public record MemberAddedEvent(UUID conversationId, UUID actorId, UUID targetId) {

}
