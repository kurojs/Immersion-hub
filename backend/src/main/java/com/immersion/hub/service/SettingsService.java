package com.immersion.hub.service;

import com.immersion.hub.dto.SettingsDto;
import com.immersion.hub.dto.UpdateSettingsRequest;
import com.immersion.hub.model.UserSettings;
import com.immersion.hub.repository.UserRepository;
import com.immersion.hub.repository.UserSettingsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

  private final UserSettingsRepository settingsRepository;
  private final UserRepository userRepository;

  public SettingsService(UserSettingsRepository settingsRepository, UserRepository userRepository) {
    this.settingsRepository = settingsRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public SettingsDto get(Long userId) {
    return toDto(getOrCreate(userId));
  }

  @Transactional
  public SettingsDto update(Long userId, UpdateSettingsRequest req) {
    UserSettings settings = getOrCreate(userId);
    settings.setNativeLang(req.nativeLang());
    settings.setPauseOnWordClick(req.pauseOnWordClick());
    settingsRepository.save(settings);
    return toDto(settings);
  }

  private UserSettings getOrCreate(Long userId) {
    return settingsRepository.findByUserId(userId)
        .orElseGet(() -> {
          UserSettings settings = new UserSettings();
          settings.setUser(userRepository.getReferenceById(userId));
          settings.setNativeLang("es");
          settings.setPauseOnWordClick(true);
          return settingsRepository.save(settings);
        });
  }

  private SettingsDto toDto(UserSettings settings) {
    return new SettingsDto(settings.getNativeLang(), settings.isPauseOnWordClick());
  }

}
