package com.mcxx.chat.auth.jwt;

import java.util.UUID;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import com.mcxx.chat.device.application.TokenSessionService;
import com.mcxx.chat.device.domain.TokenSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenSessionValidator implements OAuth2TokenValidator<Jwt> {
  private final TokenSessionService tokenSessionService;

  @Override
  public OAuth2TokenValidatorResult validate(Jwt token) {
    if (!"access".equals(token.getClaimAsString("type"))) {
      return OAuth2TokenValidatorResult.success();
    }

    UUID userId = UUID.fromString(token.getSubject());
    UUID deviceId = UUID.fromString(token.getClaimAsString("device_id"));
    Integer tokenVersion = Integer.valueOf(token.getClaimAsString("token_version"));

    TokenSession session = tokenSessionService.getSession(deviceId);
    if (session != null && session.userId().equals(userId) && session.tokenVersion().equals(tokenVersion)) {
      return OAuth2TokenValidatorResult.success();
    }

    return OAuth2TokenValidatorResult.failure(new OAuth2Error("Invalid token"));
  }

}
