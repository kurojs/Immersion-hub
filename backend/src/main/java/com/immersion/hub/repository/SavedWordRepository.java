package com.immersion.hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import com.immersion.hub.model.SavedWord;

public interface SavedWordRepository extends JpaRepository<SavedWord, Long> {
  Page<SavedWord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  Optional<SavedWord> findByIdAndUserId(Long id, Long userId);

  @Modifying
  @Query("update SavedWord w set w.content = null where w.content.id = :contentId")
  void detachContent(@Param("contentId") Long contentId);

}
