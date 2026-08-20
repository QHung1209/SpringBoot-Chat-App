package com.mcxx.chat.media.domain;

public enum MediaType {
  IMAGE,
  VIDEO,
  FILE,
  AUDIO;

  public static MediaType fromMimeType(String mimeType) {
    if (mimeType == null) return FILE;
    if (mimeType.startsWith("image/")) return IMAGE;
    if (mimeType.startsWith("video/")) return VIDEO;
    if (mimeType.startsWith("audio/")) return AUDIO;
    return FILE;
  }
}
