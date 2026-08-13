package com.mcxx.chat.chat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupRequest {
  private String name;
  private String avatarUrl;
}
