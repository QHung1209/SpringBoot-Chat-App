package com.mcxx.chat.conversation;

import java.util.UUID;
import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Entity;
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
  private String type;
  private String avatarUrl;
  private String pairKey;
  private UUID lastMessageId;
  private UUID createdBy;
}
