package com.mcxx.chat.conversation.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
  private String name;
  private List<UUID> memberIds;
}
