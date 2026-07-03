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
import com.mcxx.chat.auth.dto.AuthResponse;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.auth.dto.LoginRequest;
import com.mcxx.chat.auth.dto.RefreshRequest;
import com.mcxx.chat.auth.dto.RegisterRequest;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.device.UserDevice;
import com.mcxx.chat.device.dto.CreateDeviceRequest;
import com.mcxx.chat.device.dto.DeviceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req,
      @RequestHeader(value = "X-Device-Id", required = false) UUID deviceId,
      @RequestHeader(value = "User-Agent", required = false) String userAgent,
      @RequestHeader(value = "X-IP-Address", required = false) String ipAddress,
      @RequestHeader(value = "X-Device-Name", required = false) String deviceName, 
      @RequestHeader(value = "X-OS", required = false) String os,
      @RequestHeader(value = "X-Browser", required = false) String browser) {
      
    if (deviceId == null) {
      deviceId = java.util.UUID.randomUUID();
    }
    CreateDeviceRequest deviceReq =
        new CreateDeviceRequest(deviceId, deviceName, userAgent, ipAddress, os, browser);
    AuthResponse res = authService.register(req, deviceReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(200, res));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req,
      @RequestHeader(value = "X-Device-Id", required = false) UUID deviceId,
      @RequestHeader(value = "User-Agent", required = false) String userAgent,
      @RequestHeader(value = "X-IP-Address", required = false) String ipAddress,
      @RequestHeader(value = "X-Device-Name", required = false) String deviceName, 
      @RequestHeader(value = "X-OS", required = false) String os,
      @RequestHeader(value = "X-Browser", required = false) String browser) {

    if (deviceId == null) {
      deviceId = java.util.UUID.randomUUID();
    }
    CreateDeviceRequest deviceReq =
        new CreateDeviceRequest(deviceId, deviceName, userAgent, ipAddress, os, browser);
    AuthResponse res = authService.login(req, deviceReq);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, res));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponse>> refreshTokens(
      @Valid @RequestBody RefreshRequest req) {
    AuthResponse res = authService.refreshTokens(req.getRefreshToken());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, res));
  }

  @PostMapping("/me/logout")
  public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal AuthUser user) {
    authService.logout(user.getId(), user.getDeviceId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, null));
  }

  @PostMapping("/me/logout-all")
  public ResponseEntity<ApiResponse<Void>> logoutAll(@AuthenticationPrincipal AuthUser user) {
    authService.logoutAll(user.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, null));
  }

  @GetMapping("me/devices")
  public ResponseEntity<ApiResponse<List<DeviceResponse>>> getDevices(
      @AuthenticationPrincipal AuthUser user) {
    List<UserDevice> devices = authService.getDevices(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success(200, devices.stream().map(DeviceResponse::from).toList()));
  }

  @DeleteMapping("/me/devices/{deviceId}")
  public ResponseEntity<ApiResponse<Void>> kickout(@AuthenticationPrincipal AuthUser user,
      @PathVariable UUID deviceId) {
    authService.logout(user.getId(), deviceId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, null));
  }
}
