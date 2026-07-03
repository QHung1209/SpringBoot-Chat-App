package com.mcxx.chat.device.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateDeviceRequest {
  private UUID id;
  private String name;
  private String userAgent;
  private String ipAddress;
  private String os;
  private String browser;
}
