package com.mcxx.chat.user.dto;

import java.util.UUID;

import com.mcxx.chat.user.User;

public record UserResponse(UUID id, String username, String fullName, String email,
    String phoneNumber, String avatarUrl, String bio) {

  public static UserResponse from(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(),
        user.getPhoneNumber(), user.getAvatarUrl(), user.getBio());
  }
}
