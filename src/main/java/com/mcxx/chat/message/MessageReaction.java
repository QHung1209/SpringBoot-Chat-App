package com.mcxx.chat.message;

import java.util.UUID;
import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "message_reactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageReaction extends BaseEntity {
  private UUID messageId;
  private UUID userId;
  private String reaction;
}
