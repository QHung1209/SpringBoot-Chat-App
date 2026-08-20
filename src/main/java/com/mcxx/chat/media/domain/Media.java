package com.mcxx.chat.media.domain;

import java.util.UUID;
import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
public class Media extends BaseEntity {

  private UUID uploaderId;
  private String fileName;
  @Column(name = "key")
  private String key;
  private Long fileSize;
  private String mimeType;

  @Enumerated(EnumType.STRING)
  private MediaType type;
}
