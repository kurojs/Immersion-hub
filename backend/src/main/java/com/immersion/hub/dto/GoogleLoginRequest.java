package com.immersion.hub.dto;

import jakarta.validation.constraints.NotBlank;

public interface GoogleLoginRequest {
  @NotBlank
  String idToken();
}
