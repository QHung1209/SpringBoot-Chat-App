package com.mcxx.chat.auth.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthUser {
  private UUID id;
  private String username;
  private String fullName;
  private String email;
  private String avatarUrl;
  private UUID deviceId;
  private Integer tokenVersion;
}
