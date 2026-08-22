package com.mcxx.chat.chat.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcxx.chat.chat.domain.Message;
import com.mcxx.chat.chat.domain.MessageType;
import com.mcxx.chat.chat.dto.metadata.MessageMetadata;
import com.mcxx.chat.chat.dto.request.CreateMessage;
import com.mcxx.chat.chat.dto.response.MessageResponse;
import com.mcxx.chat.chat.event.MemberAddedEvent;
import com.mcxx.chat.chat.event.MemberLeftEvent;
import com.mcxx.chat.chat.event.MessageDeletedEvent;
import com.mcxx.chat.chat.event.MessagePinnedEvent;
import com.mcxx.chat.chat.event.MessageReactionEvent;
import com.mcxx.chat.chat.event.MessageSeenEvent;
import com.mcxx.chat.chat.event.MessageUnpinnedEvent;
import com.mcxx.chat.chat.event.TypingEvent;
import com.mcxx.chat.chat.repository.MessageRepository;
import com.mcxx.chat.media.application.MediaService;
import com.mcxx.chat.media.dto.response.MediaResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRealtimeService {

  private final SimpMessagingTemplate messagingTemplate;
  private final ConversationMemberService conversationMemberService;
  private final MessageRepository messageRepository;
  private final ObjectMapper objectMapper;
  private final MessageService messageService;
  private final MediaService mediaService;

  private void createSystemMessage(UUID conversationId, UUID senderId, MessageMetadata metadata) {
    CreateMessage createMessage = new CreateMessage();
    createMessage.setConversationId(conversationId);
    createMessage.setSenderId(senderId);
    createMessage.setType(MessageType.SYSTEM);
    JsonNode metadataNode = objectMapper.valueToTree(metadata);
    createMessage.setMetadata(metadataNode);
    Message message = messageService.createMessage(createMessage);
    messagingTemplate.convertAndSend("/topic/conversations/" + message.getConversationId(),
        metadataNode);
  }

  public void publishMessage(Message message) {
    Message replyTarget = null;
    if (message.getReplyToMessageId() != null) {
      replyTarget = messageRepository.findById(message.getReplyToMessageId()).orElse(null);
    }

    // Resolve presigned GET URLs for all media attached to this message and reply target
    List<UUID> targetIds = new ArrayList<>();
    targetIds.add(message.getId());
    if (message.getReplyToMessageId() != null) {
      targetIds.add(message.getReplyToMessageId());
    }

    Map<UUID, List<MediaResponse>> mediasByMessage = mediaService.getMediasByMessageIds(targetIds);
    List<MediaResponse> medias = mediasByMessage.getOrDefault(message.getId(), List.of());
    List<MediaResponse> replyMedias = message.getReplyToMessageId() != null
        ? mediasByMessage.getOrDefault(message.getReplyToMessageId(), List.of())
        : List.of();

    MessageResponse payload = MessageResponse.from(message, replyTarget, medias, replyMedias);

    messagingTemplate.convertAndSend("/topic/conversations/" + message.getConversationId(),
        payload);

    List<UUID> memberIds = conversationMemberService.getMemberIds(message.getConversationId());

    memberIds.stream().filter(id -> !id.equals(message.getSenderId())).forEach(id -> {
      messagingTemplate.convertAndSendToUser(id.toString(), "/queue/conversations", payload);
      messagingTemplate.convertAndSendToUser(id.toString(),
          "/queue/conversations/" + message.getConversationId(), payload);
    });
  }

  public void publishDeleteMessage(MessageDeletedEvent event) {
    messagingTemplate.convertAndSend("/topic/conversations/" + event.conversationId() + "/deleted",
        event.messageId());
  }

  public void publishSeen(MessageSeenEvent event) {
    messagingTemplate.convertAndSend("/topic/conversations/" + event.conversationId() + "/seen",
        event);
  }

  public void publishTyping(TypingEvent event) {
    messagingTemplate.convertAndSend("/topic/conversations/" + event.conversationId() + "/typing",
        event);
  }

  public void publishReaction(MessageReactionEvent event) {
    messagingTemplate
        .convertAndSend("/topic/conversations/" + event.conversationId() + "/reactions", event);
  }

  public void publishMemberAdded(MemberAddedEvent event) {
    this.createSystemMessage(event.conversationId(), event.actorId(),
        new MessageMetadata("MEMBER_ADDED", event.actorId(), event.targetId()));
    messagingTemplate.convertAndSend("/topic/conversations/" + event.conversationId() + "/members",
        event);
  }

  public void publishMemberLeft(MemberLeftEvent event) {
    this.createSystemMessage(event.conversationId(), event.actorId(),
        new MessageMetadata("MEMBER_LEFT", event.actorId(), event.targetId()));
    messagingTemplate.convertAndSend("/topic/conversations/" + event.conversationId() + "/members",
        event);
  }

  public void publishPinMessage(MessagePinnedEvent event) {
    this.createSystemMessage(event.conversationId(), event.userId(),
        new MessageMetadata("MESSAGE_PINNED", event.userId(), event.messageId()));
    messagingTemplate.convertAndSend("/topic/conversations/" + event.conversationId() + "/pins",
        event);
  }

  public void publishUnpinMessage(MessageUnpinnedEvent event) {
    this.createSystemMessage(event.conversationId(), event.userId(),
        new MessageMetadata("MESSAGE_UNPINNED", event.userId(), event.messageId()));
    messagingTemplate.convertAndSend("/topic/conversations/" + event.conversationId() + "/unpins",
        event);
  }
}
