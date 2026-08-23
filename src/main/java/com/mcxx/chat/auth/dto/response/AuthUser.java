package com.mcxx.chat.auth.dto.response;

import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthUser implements Principal {
  private UUID id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String avatarUrl;
  private UUID deviceId;
  private Integer tokenVersion;

  @Override
  public String getName() {
    return this.id.toString();
  }
}
