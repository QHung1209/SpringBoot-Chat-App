package com.mcxx.chat.auth.dto;

import java.util.UUID;
import com.mcxx.chat.user.dto.UserResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
  private UserResponse user;
  private String accessToken;
  private String refreshToken;
  private UUID deviceId;

  public static AuthResponse from(UserResponse user, String accessToken, String refreshToken, UUID deviceId) {
    AuthResponse response = new AuthResponse();
    response.setUser(user);
    response.setAccessToken(accessToken);
    response.setRefreshToken(refreshToken);
    response.setDeviceId(deviceId);
    return response;
  }
}
