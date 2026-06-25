package com.mcxx.chat.user.dto;

import java.time.Instant;
import java.util.UUID;

import com.mcxx.chat.user.User;

public record UserResponse(
    UUID id,
    String username,
    String fullName,
    String email,
    String phoneNumber,
    String avatarUrl,
    String bio,
    Instant lastLogin,
    Instant createdAt,
    Instant updatedAt) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getFullName(),
        user.getEmail(),
        user.getPhoneNumber(),
        user.getAvatarUrl(),
        user.getBio(),
        user.getLastLogin(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}
