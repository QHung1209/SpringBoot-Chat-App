package com.mcxx.chat.auth.application;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mcxx.chat.auth.dto.response.AuthResponse;
import com.mcxx.chat.auth.dto.request.LoginRequest;
import com.mcxx.chat.auth.dto.request.RegisterRequest;
import com.mcxx.chat.auth.jwt.JwtService;
import com.mcxx.chat.device.domain.TokenSession;
import com.mcxx.chat.device.repository.TokenSessionRepository;
import com.mcxx.chat.device.domain.UserDevice;
import com.mcxx.chat.device.repository.UserDeviceRepository;
import com.mcxx.chat.device.dto.request.CreateDeviceRequest;
import com.mcxx.chat.user.domain.User;
import com.mcxx.chat.user.repository.UserRepository;
import com.mcxx.chat.user.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserDeviceRepository userDeviceRepository;
  private final JwtService jwtService;
  private final TokenSessionRepository tokenSessionRepository;
  private final JwtDecoder jwtDecoder;

  @Transactional
  public AuthResponse register(RegisterRequest req, CreateDeviceRequest deviceReq) {
    if (userRepository.existsByUsername(req.getUsername())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
    }
    if (userRepository.existsByEmail(req.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    User user = new User();
    user.setUsername(req.getUsername());
    user.setPassword(passwordEncoder.encode(req.getPassword()));
    user.setFirstName(req.getFirstName());
    user.setLastName(req.getLastName());
    user.setEmail(req.getEmail());

    userRepository.save(user);

    return generateTokens(user, deviceReq);
  }

  public AuthResponse login(LoginRequest req, CreateDeviceRequest deviceReq) {
    User user = userRepository.findByUsername(req.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
    }

    return generateTokens(user, deviceReq);
  }

  private AuthResponse generateTokens(User user, CreateDeviceRequest deviceReq) {
    UserDevice device = userDeviceRepository.findById(deviceReq.getId()).map(existing -> {
      if (!existing.getUserId().equals(user.getId())) {
        UserDevice newDevice =
            new UserDevice(user.getId(), deviceReq.getName(), deviceReq.getUserAgent(),
                deviceReq.getIpAddress(), deviceReq.getOs(), deviceReq.getBrowser(), 0);
        newDevice.setId(UUID.randomUUID());
        return newDevice;
      }
      return existing;
    }).orElseGet(() -> {
      UserDevice newDevice =
          new UserDevice(user.getId(), deviceReq.getName(), deviceReq.getUserAgent(),
              deviceReq.getIpAddress(), deviceReq.getOs(), deviceReq.getBrowser(), 0);
      newDevice.setId(deviceReq.getId());
      return newDevice;
    });

    device.setIpAddress(deviceReq.getIpAddress());
    device.setUserAgent(deviceReq.getUserAgent());
    device.setOs(deviceReq.getOs());
    device.setBrowser(deviceReq.getBrowser());
    device.setName(deviceReq.getName());
    if (device.getTokenVersion() == null || device.getTokenVersion() == 0) {
      device.setTokenVersion(1);
    }

    userDeviceRepository.save(device);

    String accessToken = jwtService.generateAccessToken(user, device);
    String refreshToken = jwtService.generateRefreshToken(user, device, null);

    TokenSession session =
        new TokenSession(device.getId(), user.getId(), device.getTokenVersion(),
            jwtService.getAccessExpSeconds());

    tokenSessionRepository.save(session);

    return AuthResponse.from(UserResponse.from(user), accessToken, refreshToken, device.getId());
  }

  @Transactional
  public AuthResponse refreshTokens(String refreshToken) {
    Jwt jwt;
    try {
      jwt = jwtDecoder.decode(refreshToken);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalid");
    }
    if (!"refresh".equals(jwt.getClaimAsString("type"))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalid");
    }

    UUID userId = UUID.fromString(jwt.getSubject());
    UUID deviceId = UUID.fromString(jwt.getClaimAsString("device_id"));
    Integer tokenVersion = Integer.valueOf(jwt.getClaimAsString("token_version"));

    UserDevice device = userDeviceRepository.findByIdAndUserId(deviceId, userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalid"));

    if (!device.getTokenVersion().equals(tokenVersion)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalid");
    }

    device.setTokenVersion(tokenVersion + 1);
    userDeviceRepository.save(device);

    User user = userRepository.findById(device.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalid"));

    String newAccessToken = jwtService.generateAccessToken(user, device);
    String newRefreshToken =
        jwtService.generateRefreshToken(user, device, jwt.getExpiresAt().getEpochSecond());
    tokenSessionRepository
        .save(new TokenSession(device.getId(), user.getId(), device.getTokenVersion(),
            jwtService.getAccessExpSeconds()));

    return AuthResponse.from(UserResponse.from(user), newAccessToken, newRefreshToken,
        device.getId());
  }

  public void logout(UUID userId, UUID deviceId) {
    userDeviceRepository.findByIdAndUserId(deviceId, userId).ifPresent(device -> {
      device.setTokenVersion(device.getTokenVersion() + 1);
      userDeviceRepository.save(device);
      tokenSessionRepository.deleteById(deviceId);
    });
  }

  @Transactional
  public void logoutAll(UUID userId) {
    tokenSessionRepository.deleteByUserId(userId);
    userDeviceRepository.incrementTokenVersionByUserId(userId);
  }

  public List<UserDevice> getDevices(UUID userId) {
    return userDeviceRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }
}
