package com.immersion.hub.controller;

import com.immersion.hub.dto.PageDto;
import com.immersion.hub.dto.SaveWordRequest;
import com.immersion.hub.dto.SavedWordDto;
import com.immersion.hub.service.WordService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/words")
public class WordController {

  private final WordService wordService;

  public WordController(WordService wordService) {
    this.wordService = wordService;
  }

  @PostMapping
  public SavedWordDto save(@Valid @RequestBody SaveWordRequest req, Authentication auth) {
    return wordService.save(userId(auth), req);
  }

  @GetMapping
  public PageDto<SavedWordDto> list(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size, Authentication auth) {
    return wordService.list(userId(auth), page, size);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
    wordService.delete(id, userId(auth));
    return ResponseEntity.noContent().build();
  }

  private Long userId(Authentication auth) {
    return Long.valueOf(auth.getName());
  }
}
