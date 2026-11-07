package com.immersion.hub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.immersion.hub.exception.BadRequestException;
import com.immersion.hub.exception.NotFoundException;
import com.immersion.hub.model.Content;
import com.immersion.hub.model.Cue;
import com.immersion.hub.repository.ContentRepository;
import com.immersion.hub.repository.CueRepository;
import com.immersion.hub.subtitle.AssParser;
import com.immersion.hub.subtitle.ParsedCue;
import com.immersion.hub.subtitle.SrtParser;
import com.immersion.hub.subtitle.SubtitleParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubtitleService {

  private final ContentRepository contentRepository;

  private final CueRepository cueRepository;

  private final SrtParser srtParser;

  private final AssParser assParser;

  public SubtitleService(ContentRepository contentRepository, CueRepository cueRepository,
      SrtParser srtParser, AssParser assParser) {
    this.contentRepository = contentRepository;
    this.cueRepository = cueRepository;
    this.srtParser = srtParser;
    this.assParser = assParser;
  }

  @Transactional
  public int importSubtitles(Long contentId, Long userId, MultipartFile file) {
    Content content = contentRepository.findByIdAndUserId(contentId, userId)
        .orElseThrow(() -> new NotFoundException("Contenido no encontrado"));

    String text;
    try {
      text = new String(file.getBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new BadRequestException("No se pudo leer el archivo");
    }

    String name = file.getOriginalFilename() == null ? ""
        : file.getOriginalFilename().toLowerCase();

    SubtitleParser parser;
    if (name.endsWith(".srt")) {
      parser = srtParser;
    } else if (name.endsWith(".ass")) {
      parser = assParser;
    } else {
      throw new BadRequestException("Solo .srt o .ass");
    }

    List<ParsedCue> parsed = parser.parse(text);
    if (parsed.isEmpty()) {
      throw new BadRequestException("No se encontraron subtítulos");
    }

    cueRepository.deleteByContentId(contentId);

    List<Cue> cues = new ArrayList<>();
    int index = 0;
    for (ParsedCue pc : parsed) {
      Cue cue = new Cue();
      cue.setContent(content);
      cue.setOrderIndex(index++);
      cue.setStartMs(pc.startMs());
      cue.setEndMs(pc.endMs());
      cue.setText(pc.text());
      cues.add(cue);
    }

    cueRepository.saveAll(cues);
    return cues.size();
  }

}
