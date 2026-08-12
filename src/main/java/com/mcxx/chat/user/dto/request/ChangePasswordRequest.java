package com.mcxx.chat.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
  @NotBlank(message = "Current password is required")
  @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
  private String currentPassword;

  @NotBlank(message = "New password is required")
  @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
  private String newPassword;
}
