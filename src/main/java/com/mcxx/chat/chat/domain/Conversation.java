package com.mcxx.chat.chat.domain;

import java.util.UUID;
import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversation extends BaseEntity {
  private String name;
  @Enumerated(EnumType.STRING)
  private ConversationType type;
  private String avatarUrl;
  private String pairKey;
  private UUID lastMessageId;
  private UUID createdBy;
}
