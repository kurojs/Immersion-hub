package com.immersion.hub.repository;

import com.immersion.hub.model.Cue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CueRepository extends JpaRepository<Cue, Long> {
  List<Cue> findByContentIdOrderByOrderIndex(Long contentId);

  long countByContentId(Long contentId);

  void deleteByContentId(Long contentId);
}
