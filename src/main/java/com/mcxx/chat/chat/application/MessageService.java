package com.mcxx.chat.chat.application;

import com.mcxx.chat.chat.domain.Conversation;
import com.mcxx.chat.chat.domain.Message;
import com.mcxx.chat.chat.domain.MessageType;
import com.mcxx.chat.chat.dto.request.CreateMessage;
import com.mcxx.chat.chat.dto.request.SendMessageRequest;
import com.mcxx.chat.chat.dto.response.MessageResponse;
import com.mcxx.chat.chat.dto.response.ReactionResponse;
import com.mcxx.chat.chat.event.MessageCreatedEvent;
import com.mcxx.chat.chat.event.MessageDeletedEvent;
import com.mcxx.chat.chat.event.MessageSeenEvent;
import com.mcxx.chat.chat.repository.MessageRepository;
import com.mcxx.chat.chat.repository.MessageWithReplyProjection;
import com.mcxx.chat.media.application.MediaService;
import com.mcxx.chat.media.domain.Media;
import com.mcxx.chat.media.dto.response.MediaResponse;
import com.mcxx.chat.media.repository.MediaRepository;
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
  private final MediaService mediaService;
  private final MediaRepository mediaRepository;
  private final ApplicationEventPublisher eventPublisher;

  public List<MessageResponse> getMessages(UUID userId, UUID conversationId, Instant before,
      Instant after) {
    if (!conversationMemberService.isMember(conversationId, userId)) {
      throw new BadRequestException("Invalid conversation");
    }

    List<MessageWithReplyProjection> messages = messageRepository
        .findByConversationIdOrderByCreatedAtDesc(conversationId, before, after);

    List<UUID> messageIds = messages.stream().map(MessageWithReplyProjection::getId).toList();

    Map<UUID, List<ReactionResponse>> reactionsByMessage =
        messageReactionService.getReactions(messageIds, userId).stream()
            .collect(Collectors.groupingBy(ReactionResponse::getMessageId));

    Map<UUID, List<MediaResponse>> mediasByMessage =
        mediaService.getMediasByMessageIds(messageIds);

    if (!messages.isEmpty()) {
      this.seenMessage(userId, messages.get(0).getId());
    }

    return messages.stream().map(message -> {
      MessageResponse res = MessageResponse.from(message, mediasByMessage);
      res.setReactions(reactionsByMessage.getOrDefault(message.getId(), List.of()));
      return res;
    }).toList();
  }

  @Transactional
  public MessageResponse sendMessage(UUID senderId, SendMessageRequest request) {
    Conversation conv = null;
    if (request.getReceiverId() != null) {
      conv = conversationService.createDirectConversation(senderId, request.getReceiverId());
    } else {
      conv = conversationService.detail(request.getConversationId());
      if (!conversationMemberService.isMember(conv.getId(), senderId)) {
        throw new BadRequestException("Invalid conversation");
      }
    }
    Message replyTarget = null;
    if (request.getReplyToMessageId() != null) {
      replyTarget = messageRepository.findById(request.getReplyToMessageId())
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
    createMessage.setMediaIds(request.getMediaIds());

    Message message = createMessage(createMessage);

    eventPublisher.publishEvent(new MessageCreatedEvent(conv.getId(), message));

    List<MediaResponse> medias = mediaService
        .getMediasByMessageIds(List.of(message.getId()))
        .getOrDefault(message.getId(), List.of());

    return MessageResponse.from(message, replyTarget, medias);
  }

  @Transactional
  public Message createMessage(CreateMessage request) {
    Message message = new Message();
    message.setConversationId(request.getConversationId());
    message.setSenderId(request.getSenderId());
    message.setType(request.getType());
    message.setContent(request.getContent());
    message.setReplyToMessageId(request.getReplyToMessageId());
    message.setMetadata(request.getMetadata());

    // Attach media files if provided
    if (request.getMediaIds() != null && !request.getMediaIds().isEmpty()) {
      List<Media> medias = mediaRepository.findAllById(request.getMediaIds());
      message.setMedias(medias);
    }

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

  @Transactional
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

  public void seenConversation(UUID userId, UUID conversationId, UUID messageId) {
    if (!conversationMemberService.isMember(conversationId, userId)) {
      throw new ForbiddenException(Conversation.class, conversationId);
    }
    UUID targetMessageId = messageId;
    if (targetMessageId == null) {
      Conversation conv = conversationService.detail(conversationId);
      targetMessageId = conv.getLastMessageId();
    }
    if (targetMessageId != null) {
      conversationMemberService.seenMessage(conversationId, userId, targetMessageId);
      eventPublisher.publishEvent(new MessageSeenEvent(conversationId, targetMessageId, userId));
    }
  }

  public List<MessageResponse> getMediaMessages(UUID userId, UUID conversationId, MessageType type,
      Instant before) {
    if (!conversationMemberService.isMember(conversationId, userId)) {
      throw new BadRequestException("Invalid conversation");
    }
    String typeStr = type != null ? type.name() : null;
    List<Message> messages = messageRepository.findMediaMessages(typeStr, conversationId, before);

    List<UUID> messageIds = messages.stream().map(Message::getId).toList();
    Map<UUID, List<MediaResponse>> mediasByMessage =
        mediaService.getMediasByMessageIds(messageIds);

    return messages.stream().map(msg -> {
      MessageResponse res = MessageResponse.from(msg);
      res.setMedias(mediasByMessage.getOrDefault(msg.getId(), List.of()));
      return res;
    }).toList();
  }
}
