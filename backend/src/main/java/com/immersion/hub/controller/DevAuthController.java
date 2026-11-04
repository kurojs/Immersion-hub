package com.immersion.hub.controller;

import com.immersion.hub.dto.LoginResponse;
import com.immersion.hub.service.AuthService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/api/dev")
public class DevAuthController {

  private final AuthService authService;

  public DevAuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public LoginResponse devLogin() {
    return authService.devLogin();
  }
}
