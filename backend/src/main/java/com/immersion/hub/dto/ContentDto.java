package com.immersion.hub.dto;

import com.immersion.hub.model.Content;
import com.immersion.hub.model.ContentType;
import java.time.Instant;

public record ContentDto(Long id, ContentType type, String title,
    String sourceUrl, Instant createdAt, long cueCount) {
  public static ContentDto from(Content c, long cueCount) {
    return new ContentDto(c.getId(), c.getType(), c.getTitle(),
        c.getSourceUrl(), c.getCreatedAt(), cueCount);
  }
}
