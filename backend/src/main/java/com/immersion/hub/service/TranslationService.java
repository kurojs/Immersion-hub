package com.immersion.hub.service;

import com.immersion.hub.exception.BadRequestException;
import com.immersion.hub.exception.NotFoundException;
import com.immersion.hub.model.Cue;
import com.immersion.hub.model.CueTranslation;
import com.immersion.hub.repository.ContentRepository;
import com.immersion.hub.repository.CueRepository;
import com.immersion.hub.repository.CueTranslationRepository;
import com.immersion.hub.translation.TranslationProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TranslationService {

  private final ContentRepository contentRepository;
  private final CueRepository cueRepository;
  private final CueTranslationRepository cueTranslationRepository;
  private final TranslationProvider provider;

  private final Map<String, String> wordCache = new ConcurrentHashMap<>();

  public TranslationService(ContentRepository contentRepository, CueRepository cueRepository,
      CueTranslationRepository cueTranslationRepository, TranslationProvider provider) {
    this.contentRepository = contentRepository;
    this.cueRepository = cueRepository;
    this.cueTranslationRepository = cueTranslationRepository;
    this.provider = provider;
  }

  @Transactional
  public int translateContent(Long contentId, Long userId, String lang) {
    contentRepository.findByIdAndUserId(contentId, userId)
        .orElseThrow(() -> new NotFoundException("Content not found"));

    if (lang == null || !lang.matches("^[a-z]{2}(-[A-Za-z]{2,4})?$")) {
      throw new BadRequestException("Invalid language code: " + lang);
    }

    List<Cue> cues = cueRepository.findByContentIdOrderByOrderIndex(contentId);

    Set<Long> translatedIds = new HashSet<>();
    for (CueTranslation ct : cueTranslationRepository.findByCueContentIdAndLang(contentId, lang)) {
      translatedIds.add(ct.getCue().getId());
    }

    List<Cue> toTranslate = cues.stream()
        .filter(cue -> !translatedIds.contains(cue.getId()))
        .toList();

    int total = 0;
    for (int i = 0; i < toTranslate.size(); i += 50) {
      List<Cue> batch = toTranslate.subList(i, Math.min(i + 50, toTranslate.size()));
      List<String> texts = batch.stream().map(Cue::getText).toList();

      List<String> translations = provider.translate(texts, "ja", lang);

      List<CueTranslation> rows = new ArrayList<>();
      for (int j = 0; j < batch.size(); j++) {
        CueTranslation ct = new CueTranslation();
        ct.setCue(batch.get(j));
        ct.setLang(lang);
        ct.setText(translations.get(j));
        rows.add(ct);
      }

      cueTranslationRepository.saveAll(rows);
      total += rows.size();
    }

    return total;
  }

  public String translateWord(String text, String targetLang) {
    String key = targetLang + "|" + text;
    String cached = wordCache.get(key);
    if (cached != null) {
      return cached;
    }

    String translated = provider.translate(List.of(text), "ja", targetLang).get(0);
    wordCache.put(key, translated);
    return translated;
  }

}
