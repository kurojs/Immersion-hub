package com.immersion.hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.immersion.hub.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByGoogleSub(String googleSub);
}
