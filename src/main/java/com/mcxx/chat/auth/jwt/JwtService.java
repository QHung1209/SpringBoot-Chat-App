package com.mcxx.chat.auth.jwt;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import com.mcxx.chat.device.UserDevice;
import com.mcxx.chat.user.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Getter
public class JwtService {
  private final JwtEncoder jwtEncoder;

  @Value("${jwt.access-exp-seconds}")
  private long accessExpSeconds;

  @Value("${jwt.refresh-exp-seconds}")
  private long refreshExpSecondsDefault;

  public String generateAccessToken(User user, UserDevice device) {
    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder().issuer("auth-server").issuedAt(now)
        .expiresAt(now.plusSeconds(accessExpSeconds)).subject(user.getId().toString())
        .claim("device_id", device.getId().toString())
        .claim("token_version", device.getTokenVersion()).claim("type", "access").build();
    JwsHeader head = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(head, claims)).getTokenValue();
  }

  public String generateRefreshToken(User user, UserDevice device, Long expiresAtEpoch) {
    Instant now = Instant.now();
    Instant exp = expiresAtEpoch == null ? now.plusSeconds(refreshExpSecondsDefault)
        : Instant.ofEpochSecond(expiresAtEpoch);

    JwtClaimsSet claims = JwtClaimsSet.builder().issuer("auth-server").issuedAt(now).expiresAt(exp)
        .subject(user.getId().toString()).claim("device_id", device.getId().toString())
        .claim("token_version", device.getTokenVersion()).claim("type", "refresh").build();

    JwsHeader head = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(head, claims)).getTokenValue();
  }
}
