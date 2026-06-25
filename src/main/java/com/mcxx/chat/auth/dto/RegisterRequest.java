package com.mcxx.chat.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
  @NotBlank(message = "Username is required")
  @Size(min = 8, max = 255, message = "Username must be between 8 and 255 characters")
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
  private String password;

  @NotBlank(message = "Full name is required")
  @Size(min = 6, max = 255, message = "Full name must be between 3 and 255 characters")
  private String fullName;

  @Email(message = "Email is invalid")
  @NotBlank(message = "Email is required")
  private String email;
}
