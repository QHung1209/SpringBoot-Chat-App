package com.mcxx.chat.user.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserBasicInfo {
  private UUID id;
  private String username;
  private String fullName;
  private String email;
  private String avatar;
}
