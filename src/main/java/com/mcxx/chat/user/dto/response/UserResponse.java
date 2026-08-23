package com.mcxx.chat.user.dto.response;

import java.util.UUID;

import com.mcxx.chat.user.domain.User;

public record UserResponse(UUID id, String username, String firstName, String lastName, String email,
    String phoneNumber, String avatarUrl, String bio) {

  public static UserResponse from(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
        user.getPhoneNumber(), user.getAvatarUrl(), user.getBio());
  }
}
