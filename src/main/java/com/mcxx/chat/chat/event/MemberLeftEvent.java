package com.mcxx.chat.chat.event;

import java.util.UUID;

public record MemberLeftEvent(UUID conversationId, UUID actorId, UUID targetId) {

}
