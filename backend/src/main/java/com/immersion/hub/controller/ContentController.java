package com.immersion.hub.controller;

import org.springframework.web.bind.annotation.RestController;
import com.immersion.hub.dto.CueDto;
import com.immersion.hub.dto.CreateContentRequest;
import com.immersion.hub.dto.ContentDto;
import com.immersion.hub.service.ContentService;
import com.immersion.hub.service.SubtitleService;
import com.immersion.hub.service.TranslationService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

  private final ContentService contentService;

  private final SubtitleService subtitleService;

  private final TranslationService translationService;

  public ContentController(ContentService contentService, SubtitleService subtitleService,
      TranslationService translationService) {
    this.contentService = contentService;
    this.subtitleService = subtitleService;
    this.translationService = translationService;
  }

  @GetMapping
  public List<ContentDto> list(Authentication auth) {
    return contentService.list(userId(auth));
  }

  @PostMapping
  public ContentDto create(@Valid @RequestBody CreateContentRequest req, Authentication auth) {
    return contentService.create(userId(auth), req);
  }

  @GetMapping("/{id}")
  public ContentDto get(@PathVariable Long id, Authentication auth) {
    return contentService.get(userId(auth), id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
    contentService.delete(userId(auth), id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/subtitles")
  public ContentDto uploadSubtitles(@PathVariable Long id, @RequestParam("file") MultipartFile file,
      Authentication auth) {
    subtitleService.importSubtitles(id, userId(auth), file);
    return contentService.get(userId(auth), id);
  }

  @GetMapping("/{id}/cues")
  public List<CueDto> listCues(@PathVariable Long id,
      @RequestParam(defaultValue = "es") String lang, Authentication auth) {
    return contentService.listCues(userId(auth), id, lang);
  }

  @PostMapping("/{id}/translate")
  public Map<String, Integer> translate(@PathVariable Long id,
      @RequestParam(defaultValue = "es") String lang, Authentication auth) {
    return Map.of("translated", translationService.translateContent(id, userId(auth), lang));
  }

  private Long userId(Authentication auth) {
    return Long.valueOf(auth.getName());
  }
}
