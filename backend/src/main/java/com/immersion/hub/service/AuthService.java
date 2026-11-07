package com.immersion.hub.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.immersion.hub.dto.UserDto;
import com.immersion.hub.dto.LoginResponse;
import com.immersion.hub.exception.UnauthorizedException;
import com.immersion.hub.exception.NotFoundException;
import com.immersion.hub.model.User;
import com.immersion.hub.repository.UserRepository;
import com.immersion.hub.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final GoogleIdTokenVerifier verifier;

  public AuthService(UserRepository userRepository, JwtService jwtService,
      @Value("${app.google.client-id}") String clientId) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
        .setAudience(List.of(clientId))
        .build();
  }

  @Transactional
  public LoginResponse loginWithGoogle(String idTokenString) {
    GoogleIdToken idToken;
    try {
      idToken = verifier.verify(idTokenString);
    } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
      throw new UnauthorizedException("Could not verify the Google token");
    }
    if (idToken == null) {
      throw new UnauthorizedException("Invalid Google ID token");
    }

    GoogleIdToken.Payload p = idToken.getPayload();
    User user = upsertUser(
        p.getSubject(),
        p.getEmail(),
        (String) p.get("name"),
        (String) p.get("picture"));
    return new LoginResponse(jwtService.createToken(user.getId()), UserDto.from(user));
  }

  private User upsertUser(String sub, String email, String name, String picture) {
    User user = userRepository.findByGoogleSub(sub).orElseGet(() -> {
      User newUser = new User();
      newUser.setGoogleSub(sub);
      newUser.setEmail(email);
      newUser.setName(name);
      newUser.setPictureUrl(picture);
      newUser.setCreatedAt(Instant.now());
      return newUser;
    });

    user.setEmail(email);
    user.setName(name);
    user.setPictureUrl(picture);
    return userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public UserDto getUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found"));
    return UserDto.from(user);
  }

  @Transactional
  public LoginResponse devLogin() {
    User user = upsertUser("dev-user", "dev@local", "Dev User", null);
    return new LoginResponse(jwtService.createToken(user.getId()), UserDto.from(user));
  }

}
