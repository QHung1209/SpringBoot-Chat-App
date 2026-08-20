package com.mcxx.chat.media.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeneratePresignedUrlRequest {
  @NotBlank(message = "fileName is required")
  private String fileName;

  @NotNull(message = "fileSize is required")
  private Long fileSize;

  @NotBlank(message = "mimeType is required")
  private String mimeType;
}
