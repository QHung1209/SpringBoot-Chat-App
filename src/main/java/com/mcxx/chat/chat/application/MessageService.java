package com.mcxx.chat.chat.application;

import com.mcxx.chat.chat.domain.Conversation;
import com.mcxx.chat.chat.domain.Message;
import com.mcxx.chat.chat.dto.request.CreateMessage;
import com.mcxx.chat.chat.dto.request.SendMessageRequest;
import com.mcxx.chat.chat.dto.response.MessageResponse;
import com.mcxx.chat.chat.dto.response.ReactionResponse;
import com.mcxx.chat.chat.event.MessageCreatedEvent;
import com.mcxx.chat.chat.event.MessageDeletedEvent;
import com.mcxx.chat.chat.event.MessageSeenEvent;
import com.mcxx.chat.chat.repository.MessageRepository;
import com.mcxx.chat.chat.repository.MessageWithReplyProjection;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.mcxx.chat.common.exception.BadRequestException;
import com.mcxx.chat.common.exception.ForbiddenException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final ConversationService conversationService;
  private final ConversationMemberService conversationMemberService;
  private final MessageReactionService messageReactionService;
  private final ApplicationEventPublisher eventPublisher;

  public List<MessageResponse> getMessages(UUID userId, UUID conversationId, Instant updatedAt) {
    if (!conversationMemberService.isMember(conversationId, userId)) {
      throw new BadRequestException("Invalid conversation");
    }

    List<MessageWithReplyProjection> messages =
        messageRepository.findByConversationIdOrderByUpdatedAtDesc(conversationId, updatedAt);

    List<UUID> messageIds = messages.stream().map(MessageWithReplyProjection::getId).toList();

    Map<UUID, List<ReactionResponse>> reactionsByMessage =
        messageReactionService.getReactions(messageIds, userId).stream()
            .collect(Collectors.groupingBy(ReactionResponse::getMessageId));

    if (!messages.isEmpty()) {
      this.seenMessage(userId, messages.get(0).getId());
    }

    return messages.stream().map(message -> {
      MessageResponse res = MessageResponse.from(message);
      res.setReactions(reactionsByMessage.getOrDefault(message.getId(), List.of()));
      return res;
    }).toList();
  }

  @Transactional
  public Message sendMessage(UUID senderId, SendMessageRequest request) {
    Conversation conv = null;
    if (request.getReceiverId() != null) {
      conv = conversationService.createDirectConversation(senderId, request.getReceiverId());
    } else {
      conv = conversationService.detail(request.getConversationId());
      if (!conversationMemberService.isMember(conv.getId(), senderId)) {
        throw new BadRequestException("Invalid conversation");
      }
    }
    if (request.getReplyToMessageId() != null) {
      Message replyTarget = messageRepository.findById(request.getReplyToMessageId())
          .orElseThrow(() -> new BadRequestException("Invalid reply target message"));
      if (!replyTarget.getConversationId().equals(conv.getId())) {
        throw new BadRequestException("Cannot reply to a message from a different conversation");
      }
    }

    CreateMessage createMessage = new CreateMessage();
    createMessage.setConversationId(conv.getId());
    createMessage.setSenderId(senderId);
    createMessage.setType(request.getType());
    createMessage.setContent(request.getContent());
    createMessage.setReplyToMessageId(request.getReplyToMessageId());
    createMessage.setMediaId(request.getMediaId());
    
    Message message = createMessage(createMessage);


    eventPublisher.publishEvent(new MessageCreatedEvent(conv.getId(), message));
    return message;
  }

  @Transactional
  public Message createMessage(CreateMessage request) {
    Message message = new Message();
    message.setConversationId(request.getConversationId());
    message.setSenderId(request.getSenderId());
    message.setType(request.getType());
    message.setContent(request.getContent());
    message.setReplyToMessageId(request.getReplyToMessageId());
    message.setMediaId(request.getMediaId());
    message.setMetadata(request.getMetadata());
    message = messageRepository.save(message);

    conversationService.updateLastMessage(request.getConversationId(), message.getId(),
        Instant.now());
    this.seenMessage(request.getSenderId(), message.getId());

    return message;
  }

  public void deleteMessage(UUID senderId, UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BadRequestException("Invalid message"));

    if (!message.getSenderId().equals(senderId)) {
      throw new ForbiddenException(Message.class, messageId);
    }
    message.setContent(null);
    message.setDeletedAt(Instant.now());
    messageRepository.save(message);
    eventPublisher
        .publishEvent(new MessageDeletedEvent(message.getConversationId(), messageId, senderId));
  }

  public void seenMessage(UUID userId, UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BadRequestException("Invalid message"));

    if (!conversationMemberService.isMember(message.getConversationId(), userId)) {
      throw new ForbiddenException(Message.class, messageId);
    }
    conversationMemberService.seenMessage(message.getConversationId(), userId, messageId);

    eventPublisher
        .publishEvent(new MessageSeenEvent(message.getConversationId(), messageId, userId));
  }
}
