package com.immersion.hub.controller;

import com.immersion.hub.dto.UserDto;
import com.immersion.hub.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

  private final AuthService authService;

  public MeController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping
  public UserDto me(Authentication auth) {
    return authService.getUser(Long.parseLong(auth.getName()));
  }
}
