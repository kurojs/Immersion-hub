package com.immersion.hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.immersion.hub.model.UserSettings;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
  Optional<UserSettings> findByUserId(Long userId);
}
