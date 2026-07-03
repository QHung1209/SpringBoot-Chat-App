package com.mcxx.chat.device;
import java.util.UUID;


import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_devices")
@Getter
@Setter
@NoArgsConstructor
public class UserDevice extends BaseEntity {
  private UUID userId;

  private String name;

  private String userAgent;

  private String ipAddress;

  private String os;

  private String browser;

  private Integer tokenVersion;

  public UserDevice(UUID userId, String name, String userAgent, String ipAddress, String os,
      String browser, Integer tokenVersion) {
    this.userId = userId;
    this.name = name;
    this.userAgent = userAgent;
    this.ipAddress = ipAddress;
    this.os = os;
    this.browser = browser;
    this.tokenVersion = tokenVersion;
  }
}
