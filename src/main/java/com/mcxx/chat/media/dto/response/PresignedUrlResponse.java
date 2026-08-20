package com.mcxx.chat.media.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresignedUrlResponse {
  private UUID mediaId;
  private String uploadUrl;
}
