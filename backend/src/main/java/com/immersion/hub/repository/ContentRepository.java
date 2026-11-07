package com.immersion.hub.repository;

import com.immersion.hub.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {
  List<Content> findByUserIdOrderByCreatedAtDesc(Long userId);

  Optional<Content> findByIdAndUserId(Long id, Long userId);

}
