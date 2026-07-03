package com.mcxx.chat.userrelation;

import java.util.UUID;
import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity()
@Table(name = "user_relations")
@Getter()
@Setter()
@NoArgsConstructor
public class UserRelation extends BaseEntity {
  private UUID userLowId;

  private UUID userHighId;

  @Enumerated(EnumType.STRING)
  private RelationStatus status;

  private UUID actionUserId;
}
