package com.immersion.hub.translation;

import java.util.List;

public interface TranslationProvider {
  List<String> translate(List<String> texts, String sourceLang, String targetLang);
}
