package com.immersion.hub.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immersion.hub.dto.TranslateWordRequest;
import com.immersion.hub.dto.TranslateWordResponse;
import com.immersion.hub.service.TranslationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/translate")
public class TranslateController {

  private final TranslationService translationService;

  public TranslateController(TranslationService translationService) {
    this.translationService = translationService;
  }

  @PostMapping("/word")
  public TranslateWordResponse translateWord(@Valid @RequestBody TranslateWordRequest request) {
    return new TranslateWordResponse(
        translationService.translateWord(request.text(), request.targetLang()));
  }

}
