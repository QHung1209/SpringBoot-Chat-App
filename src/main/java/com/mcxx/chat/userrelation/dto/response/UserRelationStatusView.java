package com.mcxx.chat.userrelation.dto.response;

import java.util.UUID;

public interface UserRelationStatusView {
  UUID getTargetUserId();

  String getStatus();

  UUID getActionUserId();

  UUID getRelationId();
}
