package com.mcxx.chat.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.mcxx.chat.common.exception.BadRequestException;
import com.mcxx.chat.conversation.Conversation;
import com.mcxx.chat.conversation.ConversationMemberService;
import com.mcxx.chat.conversation.ConversationService;
import com.mcxx.chat.message.dto.request.SendMessageRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final ConversationService conversationService;
  private final ConversationMemberService conversationMemberService;


  public List<Message> getMessages(UUID conversationId, Instant createdAt) {
    return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, createdAt);
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

    return message;
  }
}
