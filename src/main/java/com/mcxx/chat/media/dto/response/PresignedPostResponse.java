package com.mcxx.chat.media.dto.response;

import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignedPostResponse {
  private UUID mediaId;
  private String url;
  private Map<String, String> fields;
}
