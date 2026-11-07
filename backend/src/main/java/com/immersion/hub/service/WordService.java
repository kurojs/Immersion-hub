package com.immersion.hub.service;

import com.immersion.hub.dto.PageDto;
import com.immersion.hub.dto.SaveWordRequest;
import com.immersion.hub.dto.SavedWordDto;
import com.immersion.hub.exception.NotFoundException;
import com.immersion.hub.model.Content;
import com.immersion.hub.model.SavedWord;
import com.immersion.hub.repository.ContentRepository;
import com.immersion.hub.repository.SavedWordRepository;
import com.immersion.hub.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;

@Service
public class WordService {

  private final SavedWordRepository savedWordRepository;
  private final ContentRepository contentRepository;
  private final UserRepository userRepository;

  public WordService(SavedWordRepository savedWordRepository, ContentRepository contentRepository,
      UserRepository userRepository) {
    this.savedWordRepository = savedWordRepository;
    this.contentRepository = contentRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public SavedWordDto save(Long userId, SaveWordRequest req) {
    Content content = null;
    if (req.contentId() != null) {
      content = contentRepository.findByIdAndUserId(req.contentId(), userId)
          .orElseThrow(() -> new NotFoundException("Content not found"));
    }

    SavedWord w = new SavedWord();
    w.setUser(userRepository.getReferenceById(userId));
    w.setContent(content);
    w.setWord(req.word());
    w.setTranslation(req.translation());
    w.setTargetLang(req.targetLang());
    w.setSentence(req.sentence());
    w.setSentenceTranslation(req.sentenceTranslation());
    w.setPositionMs(req.positionMs());
    w.setCreatedAt(Instant.now());

    savedWordRepository.save(w);
    return SavedWordDto.from(w);
  }

  @Transactional(readOnly = true)
  public PageDto<SavedWordDto> list(Long userId, int page, int size) {
    var pageRequest = PageRequest.of(page, Math.min(size, 100));
    return PageDto.from(savedWordRepository.findByUserIdOrderByCreatedAtDesc(userId, pageRequest),
        SavedWordDto::from);
  }

  @Transactional
  public void delete(Long id, Long userId) {
    SavedWord w = savedWordRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new NotFoundException("Saved word not found"));
    savedWordRepository.delete(w);
  }
}
