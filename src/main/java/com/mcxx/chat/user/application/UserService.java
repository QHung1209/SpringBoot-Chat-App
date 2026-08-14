package com.mcxx.chat.user.application;

import com.mcxx.chat.user.repository.UserRepository;
import com.mcxx.chat.user.domain.User;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mcxx.chat.common.exception.BadRequestException;
import com.mcxx.chat.common.exception.ConflictException;
import com.mcxx.chat.common.exception.NotFoundException;
import com.mcxx.chat.user.dto.request.ChangePasswordRequest;
import com.mcxx.chat.user.dto.request.UpdateProfileRequest;
import com.mcxx.chat.user.dto.response.UserResponse;
import com.mcxx.chat.user.dto.response.UserSearchView;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public List<UserSearchView> searchUsers(UUID currentUserId, String search, UUID cursor) {
    return userRepository.searchUsers(currentUserId, search, cursor);
  }

  public User getUserById(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(User.class, userId));
  }

  public UserResponse updateProfile(UUID userId, UpdateProfileRequest req) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(User.class, userId));

    if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(req.getEmail())) {
        throw new ConflictException("Email is already in use");
      }
      user.setEmail(req.getEmail());
    }
    if (req.getFirstName() != null)
      user.setFirstName(req.getFirstName());
    if (req.getLastName() != null)
      user.setLastName(req.getLastName());
    if (req.getFullName() != null && req.getFirstName() == null && req.getLastName() == null) {
      String[] parts = req.getFullName().trim().split("\\s+", 2);
      user.setFirstName(parts[0]);
      user.setLastName(parts.length > 1 ? parts[1] : "");
    }
    if (req.getPhoneNumber() != null)
      user.setPhoneNumber(req.getPhoneNumber());
    if (req.getBio() != null)
      user.setBio(req.getBio());
    if (req.getAvatarUrl() != null)
      user.setAvatarUrl(req.getAvatarUrl());

    userRepository.save(user);
    return UserResponse.from(user);
  }

  public UserResponse getProfile(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(User.class, userId));
    return UserResponse.from(user);
  }

  public void changePassword(UUID userId, ChangePasswordRequest req) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(User.class, userId));
    if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
      throw new BadRequestException("Invalid current password");
    }
    if (req.getNewPassword().equals(req.getCurrentPassword())) {
      throw new BadRequestException("New password must be different from current password");
    }
    user.setPassword(passwordEncoder.encode(req.getNewPassword()));
    userRepository.save(user);
  }
}
