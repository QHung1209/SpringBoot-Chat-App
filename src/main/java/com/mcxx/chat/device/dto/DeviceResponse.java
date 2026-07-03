package com.mcxx.chat.device.dto;

import java.time.Instant;
import java.util.UUID;
import com.mcxx.chat.device.UserDevice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeviceResponse {
  private UUID id;
  private String name;
  private String userAgent;
  private String ipAddress;
  private String os;
  private String browser;
  private Integer tokenVersion;
  private Instant updatedAt;

  public static DeviceResponse from(UserDevice device) {
    return new DeviceResponse(device.getId(), device.getName(), device.getUserAgent(),
        device.getIpAddress(), device.getOs(), device.getBrowser(), device.getTokenVersion(),
        device.getUpdatedAt());
  }
}
