package com.mcxx.chat.auth.device;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.github.f4b6a3.uuid.UuidCreator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_devices")
@Getter
@Setter
@NoArgsConstructor
public class UserDevice {
  @Id
  private UUID id;

  private UUID userId;

  private String name;

  private String userAgent;

  private String ipAddress;

  private String os;

  private String browser;

  private Integer tokenVersion;

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

  public UserDevice(
      UUID userId,
      String name,
      String userAgent,
      String ipAddress,
      String os,
      String browser,
      Integer tokenVersion) {
    this.userId = userId;
    this.name = name;
    this.userAgent = userAgent;
    this.ipAddress = ipAddress;
    this.os = os;
    this.browser = browser;
    this.tokenVersion = tokenVersion;
  }
}
