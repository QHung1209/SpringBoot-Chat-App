package com.mcxx.chat.conversation.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.conversation.dto.response.TypingEvent;
import com.mcxx.chat.message.domain.Message;
import com.mcxx.chat.message.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRealtimeService {

  private final SimpMessagingTemplate messagingTemplate;
  private final ConversationMemberService conversationMemberService;
  private final com.mcxx.chat.message.repository.MessageRepository messageRepository;

  public void publishMessage(Message message) {
    Message replyTarget = null;
    if (message.getReplyToMessageId() != null) {
      replyTarget = messageRepository.findById(message.getReplyToMessageId()).orElse(null);
    }
    MessageResponse payload = MessageResponse.from(message, replyTarget);

    messagingTemplate.convertAndSend("/topic/conversations/" + message.getConversationId(), payload);

    List<UUID> memberIds = conversationMemberService.getMemberIds(message.getConversationId());

    memberIds.stream().filter(id -> !id.equals(message.getSenderId())).forEach(id -> {
      messagingTemplate.convertAndSendToUser(id.toString(),
          "/queue/conversations/" + message.getConversationId(), payload);
    });
  }

  public void publishTyping(AuthUser authUser, UUID conversationId, boolean typing) {
    if (!conversationMemberService.isMember(conversationId, authUser.getId())) {
      throw new AccessDeniedException("User is not a member of this conversation");
    }

    TypingEvent event =
        new TypingEvent(conversationId, authUser.getId(), authUser.getFullName(), typing,
            Instant.now());

    messagingTemplate.convertAndSend("/topic/conversations/" + conversationId + "/typing", event);
  }
}
