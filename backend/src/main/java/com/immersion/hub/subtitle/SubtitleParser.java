package com.immersion.hub.subtitle;

import java.util.List;

public interface SubtitleParser {
  List<ParsedCue> parse(String subtitleContent);
}
