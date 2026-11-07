package com.immersion.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveWordRequest(
    @NotBlank @Size(max = 200) String word,
    @Size(max = 1000) String translation,
    @NotBlank @Size(max = 10) String targetLang,
    @Size(max = 2000) String sentence,
    @Size(max = 2000) String sentenceTranslation,
    Long positionMs,
    Long contentId) {
}
