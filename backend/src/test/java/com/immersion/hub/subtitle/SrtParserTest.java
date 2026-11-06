package com.immersion.hub.subtitle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class SrtParserTest {

  @Test
  void parsesTwoCues() {
    String srt = """
        1
        00:00:01,000 --> 00:00:02,000
        Hello

        2
        00:00:03,000 --> 00:00:04,000
        World
        """;

    List<ParsedCue> cues = new SrtParser().parse(srt);

    assertEquals(2, cues.size());
    assertEquals(1000, cues.get(0).startMs());
    assertEquals(2000, cues.get(0).endMs());
    assertEquals("Hello", cues.get(0).text());
    assertEquals(3000, cues.get(1).startMs());
    assertEquals(4000, cues.get(1).endMs());
    assertEquals("World", cues.get(1).text());

  }

  @Test
  void parsesCueWithHtml() {
    String srt = """
        1
        00:00:01,000 --> 00:00:02,000
        <b>Hello</b>
        """;

    List<ParsedCue> cues = new SrtParser().parse(srt);

    assertEquals(1, cues.size());
    assertEquals(1000, cues.get(0).startMs());
    assertEquals(2000, cues.get(0).endMs());
    assertEquals("Hello", cues.get(0).text());

  }

  @Test
  void parsesCueNoContent() {
    String srt = """

        """;

    List<ParsedCue> cues = new SrtParser().parse(srt);

    assertEquals(0, cues.size());

  }

}
