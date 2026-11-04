package com.immersion.hub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cue_translations", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "cue_id", "lang" })
})
@Getter
@Setter
@NoArgsConstructor
public class CueTranslation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cue_id", nullable = false)
  private Cue cue;

  @Column(length = 10, nullable = false)
  private String lang;

  @Column(length = 4000, nullable = false)
  private String text;

}
