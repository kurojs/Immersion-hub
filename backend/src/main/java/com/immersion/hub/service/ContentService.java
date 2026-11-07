package com.immersion.hub.service;

import org.springframework.stereotype.Service;
import com.immersion.hub.dto.CreateContentRequest;
import com.immersion.hub.dto.ContentDto;
import com.immersion.hub.dto.CueDto;

import com.immersion.hub.exception.BadRequestException;
import com.immersion.hub.exception.NotFoundException;

import com.immersion.hub.model.Content;
import com.immersion.hub.model.ContentType;
import com.immersion.hub.model.CueTranslation;

import com.immersion.hub.repository.ContentRepository;
import com.immersion.hub.repository.CueRepository;
import com.immersion.hub.repository.CueTranslationRepository;
import com.immersion.hub.repository.SavedWordRepository;
import com.immersion.hub.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.Instant;

@Service
public class ContentService {

  private final ContentRepository contentRepository;

  private final CueRepository cueRepository;

  private final CueTranslationRepository cueTranslationRepository;

  private final SavedWordRepository savedWordRepository;

  private final UserRepository userRepository;

  public ContentService(ContentRepository contentRepository, CueRepository cueRepository,
      CueTranslationRepository cueTranslationRepository, SavedWordRepository savedWordRepository,
      UserRepository userRepository) {
    this.contentRepository = contentRepository;
    this.cueRepository = cueRepository;
    this.cueTranslationRepository = cueTranslationRepository;
    this.savedWordRepository = savedWordRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public ContentDto create(Long userId, CreateContentRequest req) {
    if (req.type() == ContentType.MEDIA_URL) {
      String url = req.sourceUrl();
      if (url == null || !(url.startsWith("http://") || url.startsWith("https://"))) {
        throw new BadRequestException("sourceUrl is required and must start with http:// or https://");
      }
    }

    Content c = new Content();
    c.setUser(userRepository.getReferenceById(userId));
    c.setType(req.type());
    c.setTitle(req.title());
    c.setSourceUrl(req.type() == ContentType.MEDIA_URL ? req.sourceUrl() : null);
    c.setCreatedAt(Instant.now());
    c = contentRepository.save(c);
    return ContentDto.from(c, 0);
  }

  @Transactional(readOnly = true)
  public List<ContentDto> list(Long userId) {
    return contentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(c -> ContentDto.from(c, cueRepository.countByContentId(c.getId()))).toList();
  }

  @Transactional(readOnly = true)
  public ContentDto get(Long userId, Long contentId) {
    Content c = contentRepository.findByIdAndUserId(contentId, userId)
        .orElseThrow(() -> new NotFoundException("Content not found"));
    return ContentDto.from(c, cueRepository.countByContentId(c.getId()));
  }

  @Transactional
  public void delete(Long userId, Long contentId) {
    Content c = contentRepository.findByIdAndUserId(contentId, userId)
        .orElseThrow(() -> new NotFoundException("Content not found"));
    cueTranslationRepository.deleteByCueContentId(c.getId());
    savedWordRepository.detachContent(c.getId());
    cueRepository.deleteByContentId(c.getId());
    contentRepository.delete(c);
  }

  @Transactional(readOnly = true)
  public List<CueDto> listCues(Long userId, Long contentId, String lang) {
    Content c = contentRepository.findByIdAndUserId(contentId, userId)
        .orElseThrow(() -> new NotFoundException("Content not found"));

    Map<Long, String> translationMap = cueTranslationRepository
        .findByCueContentIdAndLang(c.getId(), lang).stream()
        .collect(Collectors.toMap(t -> t.getCue().getId(), CueTranslation::getText));

    return cueRepository.findByContentIdOrderByOrderIndex(c.getId()).stream()
        .map(cue -> new CueDto(cue.getId(), cue.getOrderIndex(), cue.getStartMs(), cue.getEndMs(),
            cue.getText(), translationMap.get(cue.getId())))
        .toList();
  }

}
