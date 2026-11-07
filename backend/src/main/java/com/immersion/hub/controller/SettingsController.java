package com.immersion.hub.controller;

import com.immersion.hub.dto.SettingsDto;
import com.immersion.hub.dto.UpdateSettingsRequest;
import com.immersion.hub.service.SettingsService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/me/settings")
public class SettingsController {

  private final SettingsService settingsService;

  public SettingsController(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @GetMapping
  public SettingsDto get(Authentication auth) {
    return settingsService.get(userId(auth));
  }

  @PutMapping
  public SettingsDto update(Authentication auth, @RequestBody @Valid UpdateSettingsRequest req) {
    return settingsService.update(userId(auth), req);
  }

  private Long userId(Authentication auth) {
    return Long.valueOf(auth.getName());
  }

}
