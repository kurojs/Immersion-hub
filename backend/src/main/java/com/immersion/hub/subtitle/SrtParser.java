package com.immersion.hub.subtitle;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SrtParser implements SubtitleParser {

  @Override
  public List<ParsedCue> parse(String content) {
    List<ParsedCue> result = new ArrayList<>();

    String clean = content.replace("\uFEFF", "").replace("\r\n", "\n").trim();

    String[] blocks = clean.split("\n\n+");

    for (String block : blocks) {
      String[] lines = block.split("\n");

      int timeLine = -1;
      for (int i = 0; i < lines.length; i++) {
        if (lines[i].contains("-->")) {
          timeLine = i;
          break;
        }
      }
      if (timeLine == -1)
        continue;

      String[] times = lines[timeLine].split("-->");
      long startTime = toMs(times[0].trim());
      long endTime = toMs(times[1].trim());
      String text = String.join("\n", Arrays.copyOfRange(lines, timeLine + 1, lines.length))
          .replaceAll("<[^>]+>", "").trim();
      if (!text.isEmpty()) {
        result.add(new ParsedCue(startTime, endTime, text));
      }

    }

    return result;
  }

  private long toMs(String t) {
    String[] p = t.trim().replace(',', '.').split("[:.]");
    long h = Long.parseLong(p[0]);
    long m = Long.parseLong(p[1]);
    long s = Long.parseLong(p[2]);
    long ms = Long.parseLong(p[3]);
    return ((h * 60 + m) * 60 + s) * 1000 + ms;
  }
}
