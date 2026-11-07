package com.immersion.hub.subtitle;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Component
public class AssParser implements SubtitleParser {

  @Override
  public List<ParsedCue> parse(String content) {
    List<ParsedCue> result = new ArrayList<>();

    String clean = content.replace("\uFEFF", "").replace("\r\n", "\n");

    for (String line : clean.split("\n")) {
      if (!line.startsWith("Dialogue:")) {
        continue;
      }

      String[] parts = line.substring(9).split(",", 10);
      if (parts.length < 10) {
        continue;
      }

      long startTime = toMs(parts[1]);
      long endTime = toMs(parts[2]);

      String text = parts[9]
          .replaceAll("\\{[^}]*\\}", "")
          .replace("\\N", "\n")
          .trim();

      if (!text.isEmpty()) {
        result.add(new ParsedCue(startTime, endTime, text));
      }
    }

    result.sort(Comparator.comparingLong(ParsedCue::startMs));

    return result;
  }

  private long toMs(String t) {
    String[] p = t.trim().replace(',', '.').split("[:.]");
    long h = Long.parseLong(p[0]);
    long m = Long.parseLong(p[1]);
    long s = Long.parseLong(p[2]);
    long cs = Long.parseLong(p[3]);
    return ((h * 60 + m) * 60 + s) * 1000 + cs * 10;
  }
}
