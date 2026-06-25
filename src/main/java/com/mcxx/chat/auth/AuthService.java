package com.mcxx.chat.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mcxx.chat.auth.dto.RegisterRequest;
import com.mcxx.chat.user.User;
import com.mcxx.chat.user.UserRepository;
import com.mcxx.chat.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserResponse register(RegisterRequest req) {
    if (userRepository.existsByUsername(req.getUsername())) {
      throw new RuntimeException("Username already exists");
    }
    if (userRepository.existsByEmail(req.getEmail())) {
      throw new RuntimeException("Email already exists");
    }
    User user = new User();
    user.setUsername(req.getUsername());
    user.setPassword(passwordEncoder.encode(req.getPassword()));
    user.setFullName(req.getFullName());
    user.setEmail(req.getEmail());
    return UserResponse.from(userRepository.save(user));
  }
}
