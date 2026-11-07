package com.immersion.hub.dto;

import com.immersion.hub.model.SavedWord;
import java.time.Instant;

public record SavedWordDto(Long id, String word, String translation, String targetLang,
    String sentence, String sentenceTranslation, Long positionMs, Long contentId,
    String contentTitle, Instant createdAt) {

  public static SavedWordDto from(SavedWord w) {
    return new SavedWordDto(w.getId(), w.getWord(), w.getTranslation(), w.getTargetLang(),
        w.getSentence(), w.getSentenceTranslation(), w.getPositionMs(),
        w.getContent() != null ? w.getContent().getId() : null,
        w.getContent() != null ? w.getContent().getTitle() : null,
        w.getCreatedAt());
  }
}
