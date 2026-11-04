package com.immersion.hub.dto;

import java.time.Instant;

public record ErrorDto(int status, String message, Instant timestamp) {
}
