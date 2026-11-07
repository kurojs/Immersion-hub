package com.immersion.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateSettingsRequest(
    @NotBlank @Pattern(regexp = "^[a-z]{2}(-[A-Za-z]{2,4})?$") String nativeLang,
    boolean pauseOnWordClick) {
}
