package com.mcxx.chat.auth.jwt;

import java.util.UUID;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import com.mcxx.chat.device.TokenSessionRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenSessionValidator implements OAuth2TokenValidator<Jwt> {
  private final TokenSessionRepository tokenSessionRepository;

  @Override
  public OAuth2TokenValidatorResult validate(Jwt token) {
    if (!"access".equals(token.getClaimAsString("type"))) {
      return OAuth2TokenValidatorResult.success();
    }

    UUID deviceId = UUID.fromString(token.getClaimAsString("device_id"));

    return tokenSessionRepository.findById(deviceId)
        .map(s -> OAuth2TokenValidatorResult.success())
        .orElseGet(() -> OAuth2TokenValidatorResult.failure(
            new OAuth2Error("Invalid token")));
  }

}
