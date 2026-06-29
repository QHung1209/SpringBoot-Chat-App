package com.mcxx.chat.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateDeviceRequest {
  private String name;
  private String userAgent;
  private String ipAddress;
  private String os;
  private String browser;
}
