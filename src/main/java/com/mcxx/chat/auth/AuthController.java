package com.mcxx.chat.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.device.UserDevice;
import com.mcxx.chat.auth.dto.AuthResponse;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.auth.dto.CreateDeviceRequest;
import com.mcxx.chat.auth.dto.DeviceResponse;
import com.mcxx.chat.auth.dto.LoginRequest;
import com.mcxx.chat.auth.dto.RefreshRequest;
import com.mcxx.chat.auth.dto.RegisterRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req,
      @RequestHeader("User-Agent") String userAgent,
      @RequestHeader("X-IP-Address") String ipAddress,
      @RequestHeader("X-Device-Name") String deviceName, @RequestHeader("X-OS") String os,
      @RequestHeader("X-Browser") String browser) {
    CreateDeviceRequest deviceReq =
        new CreateDeviceRequest(deviceName, userAgent, ipAddress, os, browser);
    AuthResponse res = authService.register(req, deviceReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(res);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req,
      @RequestHeader("User-Agent") String userAgent,
      @RequestHeader("X-IP-Address") String ipAddress,
      @RequestHeader("X-Device-Name") String deviceName, @RequestHeader("X-OS") String os,
      @RequestHeader("X-Browser") String browser) {
    CreateDeviceRequest deviceReq =
        new CreateDeviceRequest(deviceName, userAgent, ipAddress, os, browser);
    AuthResponse res = authService.login(req, deviceReq);
    return ResponseEntity.status(HttpStatus.OK).body(res);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshTokens(@Valid @RequestBody RefreshRequest req) {
    AuthResponse res = authService.refreshTokens(req.getRefreshToken());
    return ResponseEntity.status(HttpStatus.OK).body(res);
  }

  @PostMapping("/me/logout")
  public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthUser user) {
    authService.logout(user.getId(), user.getDeviceId());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/me/logout-all")
  public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal AuthUser user) {
    authService.logoutAll(user.getId());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("me/devices")
  public ResponseEntity<List<DeviceResponse>> getDevices(@AuthenticationPrincipal AuthUser user) {
    List<UserDevice> devices = authService.getDevices(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(devices.stream().map(DeviceResponse::from).toList());
  }

  @DeleteMapping("/me/devices/{deviceId}")
  public ResponseEntity<Void> kickout(@AuthenticationPrincipal AuthUser user,
      @PathVariable UUID deviceId) {
    authService.logout(user.getId(), deviceId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
