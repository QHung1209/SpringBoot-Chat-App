package com.mcxx.chat.auth.jwt;

import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.common.cache.UserCacheService;
import com.mcxx.chat.user.dto.UserBasicInfo;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  private final UserCacheService userCache;


  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    UserBasicInfo info = userCache.getUserBasicInfo(UUID.fromString(jwt.getSubject()));
    AuthUser authUser = new AuthUser(UUID.fromString(info.getId().toString()), info.getUsername(),
        info.getFullName(), info.getEmail(), info.getAvatar(),
        UUID.fromString(jwt.getClaimAsString("device_id")),
        Integer.valueOf(jwt.getClaimAsString("token_version")));
    return new UsernamePasswordAuthenticationToken(authUser, jwt,
        java.util.Collections.emptyList());
  }
}
