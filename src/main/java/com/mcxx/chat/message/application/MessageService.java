package com.mcxx.chat.message.application;

import com.mcxx.chat.conversation.domain.Conversation;
import com.mcxx.chat.message.repository.MessageRepository;
import com.mcxx.chat.message.domain.Message;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.mcxx.chat.common.exception.BadRequestException;
import com.mcxx.chat.common.exception.ForbiddenException;
import com.mcxx.chat.conversation.application.ConversationMemberService;
import com.mcxx.chat.conversation.application.ConversationService;
import com.mcxx.chat.message.dto.request.SendMessageRequest;
import com.mcxx.chat.message.dto.response.MessageResponse;
import com.mcxx.chat.message.dto.response.ReactionResponse;
import com.mcxx.chat.message.event.MessageCreatedEvent;
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

    List<Message> messages =
        messageRepository.findByConversationIdOrderByUpdatedAtDesc(conversationId, updatedAt);

    List<UUID> messageIds = messages.stream().map(Message::getId).toList();

    Map<UUID, List<ReactionResponse>> reactionsByMessage =
        messageReactionService.getReactions(messageIds, userId).stream()
            .collect(Collectors.groupingBy(ReactionResponse::getMessageId));

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
    Message message = new Message();
    message.setConversationId(conv.getId());
    message.setSenderId(senderId);
    message.setType(request.getType());
    message.setContent(request.getContent());
    message.setReplyToMessageId(request.getReplyToMessageId());
    message.setMediaId(request.getMediaId());
    message = messageRepository.save(message);

    conversationService.updateLastMessage(conv.getId(), message.getId(), Instant.now());
    
    eventPublisher.publishEvent(new MessageCreatedEvent(conv.getId(), message));
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
  }
}
