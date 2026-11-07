package com.immersion.hub.repository;

import com.immersion.hub.model.CueTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CueTranslationRepository extends JpaRepository<CueTranslation, Long> {

  List<CueTranslation> findByCueContentIdAndLang(Long contentId, String lang);

  void deleteByCueContentId(Long contentId);
}
