package com.immersion.hub.dto;

import com.immersion.hub.model.ContentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateContentRequest(
    @NotNull ContentType type,
    @NotBlank @Size(max = 200) String title,
    @Size(max = 1000) String sourceUrl) {
}
