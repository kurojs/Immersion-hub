package com.immersion.hub.subtitle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class AssParserTest {

  private static final String ASS = """
      [Script Info]
      Title: Test

      [Events]
      Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
      Dialogue: 0,0:00:01.00,0:00:03.50,Default,,0,0,0,,{\\an8}こんにちは、元気ですか？
      Dialogue: 0,0:00:04.00,0:00:06.20,Default,,0,0,0,,はい、\\N元気です。
      """;

  @Test
  void parsesTwoDialogues() {
    List<ParsedCue> cues = new AssParser().parse(ASS);

    assertEquals(2, cues.size());
    assertEquals(1000, cues.get(0).startMs());
    assertEquals(3500, cues.get(0).endMs());
    assertEquals("こんにちは、元気ですか？", cues.get(0).text());
    assertEquals(4000, cues.get(1).startMs());
    assertEquals(6200, cues.get(1).endMs());
    assertEquals("はい、\n元気です。", cues.get(1).text());
  }

  @Test
  void keepsCommasInsideText() {
    String ass = "Dialogue: 0,0:00:01.00,0:00:02.00,Default,,0,0,0,,Hola, mundo, adiós";

    List<ParsedCue> cues = new AssParser().parse(ass);

    assertEquals(1, cues.size());
    assertEquals("Hola, mundo, adiós", cues.get(0).text());
  }

  @Test
  void ignoresNonDialogueLines() {
    List<ParsedCue> cues = new AssParser().parse("[Events]\nFormat: Layer, Start\n");

    assertEquals(0, cues.size());
  }
}
