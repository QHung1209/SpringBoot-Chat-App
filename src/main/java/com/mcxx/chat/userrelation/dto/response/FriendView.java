package com.mcxx.chat.userrelation.dto.response;

import java.util.UUID;

public interface FriendView {
  UUID getUserId();
  String getFirstName();
  String getLastName();
  String getFullName();
  String getAvatarUrl();
  String getBio();
  UUID getRelationId();
}
