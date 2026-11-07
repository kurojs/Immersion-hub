package com.immersion.hub.translation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class FakeTranslationProvider implements TranslationProvider {
  @Override
  public List<String> translate(List<String> texts, String sourceLang, String targetLang) {
    return texts.stream()
        .map(text -> "[" + targetLang + "] " + text)
        .toList();
  }
}
