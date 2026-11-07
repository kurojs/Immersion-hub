package com.immersion.hub.translation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.immersion.hub.exception.BadRequestException;
import java.util.List;

@Component
@Profile("!dev")
public class GoogleTranslationProvider implements TranslationProvider {
  private final String apiKey;
  private final RestClient restClient = RestClient.create();

  public GoogleTranslationProvider(@Value("${app.google.translate-key}") String apiKey) {
    this.apiKey = apiKey;
  }

  record Req(List<String> q, String source, String target, String format) {
  }

  record Res(Data data) {
    record Data(List<Translation> translations) {
    }

    record Translation(String translatedText) {
    }
  }

  @Override
  public List<String> translate(List<String> texts, String sourceLang, String targetLang) {
    try {
      Req request = new Req(texts, sourceLang, targetLang, "text");
      Res response = restClient.post()
          .uri("https://translation.googleapis.com/language/translate/v2?key=" + apiKey)
          .contentType(MediaType.APPLICATION_JSON)
          .body(request)
          .retrieve()
          .body(Res.class);
      return response.data().translations().stream()
          .map(Res.Translation::translatedText)
          .toList();
    } catch (Exception e) {
      throw new BadRequestException("Could not translate. Check your Google API key.");
    }
  }
}
