package com.mcxx.chat.media.application;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

  public record PresignedPostData(String url, Map<String, String> fields) {}

  private final S3Presigner s3Presigner;

  private final S3Client s3Client;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  @Value("${aws.s3.region}")
  private String region;

  @Value("${aws.s3.access-key}")
  private String accessKey;

  @Value("${aws.s3.secret-key}")
  private String secretKey;

  @Value("${aws.s3.cdn-url:}")
  private String cdnUrl;

  public String generatePresignedUrl(String key, String contentType, Duration expiration) {
    PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(expiration).putObjectRequest(objectRequest).build();

    return s3Presigner.presignPutObject(presignRequest).url().toExternalForm();
  }

  public PresignedPostData generatePresignedPost(String key, String contentType, long minSize,
      long maxSize, Duration expiration) {
    Instant now = Instant.now();
    String amzDate = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
        .withZone(ZoneOffset.UTC).format(now);
    String dateStamp = DateTimeFormatter.ofPattern("yyyyMMdd")
        .withZone(ZoneOffset.UTC).format(now);
    String expirationIso = DateTimeFormatter.ISO_INSTANT.format(now.plus(expiration));
    String credential = accessKey + "/" + dateStamp + "/" + region + "/s3/aws4_request";

    String policyJson = String.format(
        """
        {
          "expiration": "%s",
          "conditions": [
            {"bucket": "%s"},
            {"key": "%s"},
            {"Content-Type": "%s"},
            ["content-length-range", %d, %d],
            {"x-amz-algorithm": "AWS4-HMAC-SHA256"},
            {"x-amz-credential": "%s"},
            {"x-amz-date": "%s"}
          ]
        }
        """,
        expirationIso, bucketName, key, contentType, minSize, maxSize, credential, amzDate
    );

    String base64Policy = Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));
    String signature = calculateSignature(base64Policy, dateStamp);

    String url = "https://" + bucketName + ".s3." + region + ".amazonaws.com";

    Map<String, String> fields = new LinkedHashMap<>();
    fields.put("key", key);
    fields.put("Content-Type", contentType);
    fields.put("bucket", bucketName);
    fields.put("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
    fields.put("X-Amz-Credential", credential);
    fields.put("X-Amz-Date", amzDate);
    fields.put("Policy", base64Policy);
    fields.put("X-Amz-Signature", signature);

    return new PresignedPostData(url, fields);
  }

  private String calculateSignature(String stringToSign, String dateStamp) {
    try {
      byte[] kSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
      byte[] kDate = hmacSha256(kSecret, dateStamp);
      byte[] kRegion = hmacSha256(kDate, region);
      byte[] kService = hmacSha256(kRegion, "s3");
      byte[] kSigning = hmacSha256(kService, "aws4_request");
      byte[] signatureBytes = hmacSha256(kSigning, stringToSign);
      return HexFormat.of().formatHex(signatureBytes);
    } catch (Exception e) {
      throw new RuntimeException("Failed to calculate S3 signature", e);
    }
  }

  private byte[] hmacSha256(byte[] key, String data) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(key, "HmacSHA256"));
    return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
  }

  public String generatePresignedGetUrl(String key, Duration expiration) {
    if (cdnUrl != null && !cdnUrl.isBlank()) {
      String base = cdnUrl.startsWith("http://") || cdnUrl.startsWith("https://")
          ? cdnUrl
          : "https://" + cdnUrl;
      return base.replaceAll("/+$", "") + "/" + key;
    }

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
