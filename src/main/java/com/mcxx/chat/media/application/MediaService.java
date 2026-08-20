package com.mcxx.chat.media.application;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mcxx.chat.media.domain.Media;
import com.mcxx.chat.media.domain.MediaType;
import com.mcxx.chat.media.dto.request.GeneratePresignedUrlRequest;
import com.mcxx.chat.media.dto.response.MediaResponse;
import com.mcxx.chat.media.dto.response.PresignedUrlResponse;
import com.mcxx.chat.media.repository.MediaRepository;
import com.mcxx.chat.media.repository.MessageMediaProjection;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaService {
  private final MediaRepository mediaRepository;
  private final S3Service s3Service;

  private static final Duration PRESIGNED_VIEW_EXPIRY = Duration.ofHours(1);

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  @Transactional
  public PresignedUrlResponse generatePresignedUrl(UUID uploaderId,
      GeneratePresignedUrlRequest request) {
    MediaType mediaType = MediaType.fromMimeType(request.getMimeType());
    String s3Key = "uploads/" + uploaderId + "/" + UUID.randomUUID() + "_" + request.getFileName();
    String uploadUrl =
        s3Service.generatePresignedUrl(s3Key, request.getMimeType(), Duration.ofMinutes(15));

    Media media = new Media();
    media.setUploaderId(uploaderId);
    media.setFileName(request.getFileName());
    media.setKey(s3Key);
    media.setFileSize(request.getFileSize());
    media.setMimeType(request.getMimeType());
    media.setType(mediaType);
    media = mediaRepository.save(media);
    return new PresignedUrlResponse(media.getId(), uploadUrl);
  }

  @Transactional
  public void deleteMedia(UUID mediaId, UUID requesterId) {
    Media media = mediaRepository.findById(mediaId)
        .orElseThrow(() -> new RuntimeException("Media not found"));

    if (!media.getUploaderId().equals(requesterId)) {
      throw new RuntimeException("Forbidden: you don't own this media");
    }

    s3Service.deleteObject(media.getKey());
    mediaRepository.delete(media);
  }

  public Map<UUID, MediaResponse> getMediaWithPresignedUrls(List<UUID> mediaIds) {
    if (mediaIds == null || mediaIds.isEmpty()) {
      return Map.of();
    }
    List<Media> medias = mediaRepository.findByIdIn(mediaIds);
    return medias.stream().collect(Collectors.toMap(Media::getId, media -> {
      String viewUrl = s3Service.generatePresignedGetUrl(media.getKey(), PRESIGNED_VIEW_EXPIRY);
      return MediaResponse.from(media, viewUrl);
    }));
  }

  /**
   * Batch-load media for a list of message IDs.
   * Returns Map<messageId, List<MediaResponse>> with presigned view URLs, preserving position order.
   */
  public Map<UUID, List<MediaResponse>> getMediasByMessageIds(List<UUID> messageIds) {
    if (messageIds == null || messageIds.isEmpty()) {
      return Map.of();
    }

    // Step 1: get message_id → media_id mappings (ordered by position)
    List<MessageMediaProjection> mappings = mediaRepository.findMessageMediaMappings(messageIds);

    // Step 2: collect all distinct mediaIds
    List<UUID> mediaIds = mappings.stream()
        .map(MessageMediaProjection::getMediaId)
        .distinct()
        .toList();

    // Step 3: batch-load media + generate presigned URLs
    Map<UUID, MediaResponse> mediaById = getMediaWithPresignedUrls(mediaIds);

    // Step 4: group by messageId preserving order
    Map<UUID, List<MediaResponse>> result = new LinkedHashMap<>();
    for (MessageMediaProjection m : mappings) {
      MediaResponse mr = mediaById.get(m.getMediaId());
      if (mr != null) {
        result.computeIfAbsent(m.getMessageId(), k -> new ArrayList<>()).add(mr);
      }
    }
    return result;
  }
}
