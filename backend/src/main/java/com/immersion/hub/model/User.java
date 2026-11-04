package com.immersion.hub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "google_sub", nullable = false, unique = true)
  private String googleSub;
  @Column(name = "email", nullable = false, unique = true)
  private String email;
  private String name;
  private String pictureUrl;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

}
