package com.immersion.hub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_words")
@Getter
@Setter
@NoArgsConstructor
public class SavedWord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id")
  private Content content;

  @Column(length = 200, nullable = false)
  private String word;

  @Column(length = 1000)
  private String translation;

  @Column(length = 10)
  private String targetLang;

  @Column(length = 2000)
  private String sentence;

  @Column(length = 2000)
  private String sentenceTranslation;

  private Long positionMs;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

}
