package com.immersion.hub.dto;

import com.immersion.hub.model.User;

public record UserDto(
    Long id,
    String email,
    String name,
    String pictureUrl) {
  public static UserDto from(User user) {
    return new UserDto(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getPictureUrl());
  }
}
