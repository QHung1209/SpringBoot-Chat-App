package com.mcxx.chat.userrelation.dto;

import java.util.UUID;

public interface FriendView {
  UUID getUserId();
  String getFullName();
  String getAvatarUrl();
  String getBio();
  UUID getRelationId();
}
