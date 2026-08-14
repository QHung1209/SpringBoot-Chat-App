package com.mcxx.chat.user.dto.response;

import java.util.UUID;

public interface UserSearchView {
  UUID getId();

  String getUsername();

  String getFirstName();

  String getLastName();

  String getFullName();

  String getEmail();

  String getAvatarUrl();

  String getBio();

  String getRelationStatus();

  UUID getRelationId();

  UUID getActionUserId();
}
