package com.mcxx.chat.conversation;

import java.util.UUID;
import com.mcxx.chat.common.util.BaseEntity;
import com.mcxx.chat.conversation.constants.ConversationRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conversation_members")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMember extends BaseEntity {
  private UUID conversationId;
  private String name;
  private UUID userId;
  @Enumerated(EnumType.STRING)
  private ConversationRole role;
  private UUID hiddenAtMessageId;
  private UUID lastReadMessageId;

  public ConversationMember(UUID conversationId, UUID userId) {
    this.conversationId = conversationId;
    this.userId = userId;
    this.role = ConversationRole.MEMBER;
  }
}
