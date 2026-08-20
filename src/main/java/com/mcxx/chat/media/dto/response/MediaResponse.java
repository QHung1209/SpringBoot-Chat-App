package com.mcxx.chat.media.dto.response;

import java.time.Instant;
import java.util.UUID;
import com.mcxx.chat.media.domain.Media;
import com.mcxx.chat.media.domain.MediaType;
import lombok.Data;

@Data
public class MediaResponse {
  private UUID id;
  private String fileName;
  private String viewUrl;
  private Long fileSize;
  private String mimeType;
  private MediaType type;
  private Instant createdAt;

  public static MediaResponse from(Media media, String viewUrl) {
    if (media == null) return null;
    MediaResponse res = new MediaResponse();
    res.setId(media.getId());
    res.setFileName(media.getFileName());
    res.setViewUrl(viewUrl);
    res.setFileSize(media.getFileSize());
    res.setMimeType(media.getMimeType());
    res.setType(media.getType());
    res.setCreatedAt(media.getCreatedAt());
    return res;
  }
}
