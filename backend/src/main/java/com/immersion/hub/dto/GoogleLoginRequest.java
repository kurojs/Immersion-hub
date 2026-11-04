package com.immersion.hub.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(@NotBlank String idToken) {
}
