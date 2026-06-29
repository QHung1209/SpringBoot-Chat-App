package com.mcxx.chat.auth.dto;

import com.mcxx.chat.user.dto.UserResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
  private UserResponse user;
  private String accessToken;
  private String refreshToken;

  public static AuthResponse from(UserResponse user, String accessToken, String refreshToken) {
    AuthResponse response = new AuthResponse();
    response.setUser(user);
    response.setAccessToken(accessToken);
    response.setRefreshToken(refreshToken);
    System.out.println("haha");
    return response;
  }
}
