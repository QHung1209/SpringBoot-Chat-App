package com.mcxx.chat.chat.dto.metadata;

import java.util.UUID;

public record MessageMetadata(String event, UUID actorId, UUID targetId) {

}
