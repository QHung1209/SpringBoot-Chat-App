package com.mcxx.chat.conversation.dto.response;

import com.mcxx.chat.conversation.domain.ConversationMember;
import java.time.Instant;
import java.util.UUID;
import com.mcxx.chat.conversation.domain.ConversationRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationMemberResponse {
  private UUID id;
  private UUID conversationId;
  private String name;
  private UUID userId;
  private ConversationRole role;
  private UUID hiddenAtMessageId;
  private UUID lastReadMessageId;
  private Instant createdAt;
  private Instant updatedAt;

  public static ConversationMemberResponse from(ConversationMember member) {
    return new ConversationMemberResponse(member.getId(), member.getConversationId(),
        member.getName(), member.getUserId(), member.getRole(), member.getHiddenAtMessageId(),
        member.getLastReadMessageId(), member.getCreatedAt(), member.getUpdatedAt());
  }
}
