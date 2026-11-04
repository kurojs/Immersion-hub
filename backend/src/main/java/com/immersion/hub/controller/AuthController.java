package com.immersion.hub.controller;

import com.immersion.hub.dto.GoogleLoginRequest;
import com.immersion.hub.dto.LoginResponse;
import com.immersion.hub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/google")
  public LoginResponse google(@Valid @RequestBody GoogleLoginRequest request) {
    return authService.loginWithGoogle(request.idToken());
  }
}
