package com.mcxx.chat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {
  private int status;
  private T result;
  private ApiError error;

  public static <T> ApiResponse<T> success(int status, T result) {
    return new ApiResponse<>(status, result, null);
  }

  public static <T> ApiResponse<T> error(int status, String key, String message) {
    return new ApiResponse<>(status, null, new ApiError(message, key));
  }
}
