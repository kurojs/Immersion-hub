package com.immersion.hub.dto;

public record CueDto(Long id, int orderIndex, long startMs, long endMs, String text, String translation) {
}
