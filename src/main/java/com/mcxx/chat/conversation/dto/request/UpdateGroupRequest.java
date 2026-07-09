package com.mcxx.chat.conversation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupRequest {
  private String name;
  private String avatarUrl;
}
