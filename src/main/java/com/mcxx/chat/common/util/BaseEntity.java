package com.mcxx.chat.common.util;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
  @Id
  private UUID id;

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  @PrePersist
  public void pre() {
    if (id == null) {
      id = UuidCreator.getTimeOrderedEpoch();
    }
  }
}
