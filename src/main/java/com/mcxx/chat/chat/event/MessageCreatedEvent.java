package com.mcxx.chat.chat.event;

import java.util.UUID;
import com.mcxx.chat.chat.domain.Message;

public record MessageCreatedEvent(UUID conversationId, Message message) {

}
