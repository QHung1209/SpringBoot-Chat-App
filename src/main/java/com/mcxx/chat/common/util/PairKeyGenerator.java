package com.mcxx.chat.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PairKeyGenerator {
  public String generate(UUID userId1, UUID userId2) {
    UUID a = userId1.compareTo(userId2) < 0 ? userId1 : userId2;
    UUID b = userId1.compareTo(userId2) < 0 ? userId2 : userId1;

    String raw = a.toString() + ":" + b.toString();

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] fullHash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
      byte[] truncated = Arrays.copyOf(fullHash, 16);

      StringBuilder sb = new StringBuilder();
      for (byte b2 : truncated) {
        sb.append(String.format("%02x", b2));
      }
      return sb.toString();

    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
