package com.mcxx.chat.media.application;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Presigner s3Presigner;

  private final S3Client s3Client;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  public String generatePresignedUrl(String key, String contentType, Duration expiration) {
    PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(expiration).putObjectRequest(objectRequest).build();

    return s3Presigner.presignPutObject(presignRequest).url().toExternalForm();
  }

  public String generatePresignedGetUrl(String key, Duration expiration) {
    GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(expiration).getObjectRequest(objectRequest).build();

    return s3Presigner.presignGetObject(presignRequest).url().toExternalForm();
  }

  public void deleteObject(String key) {
    DeleteObjectRequest objectRequest =
        DeleteObjectRequest.builder().bucket(bucketName).key(key).build();

    s3Client.deleteObject(objectRequest);
  }
}
