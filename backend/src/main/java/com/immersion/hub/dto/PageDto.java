package com.immersion.hub.dto;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public record PageDto<T>(List<T> content, int page, int size, long totalElements) {
  public static <E, T> PageDto<T> from(Page<E> p, Function<E, T> mapper) {
    return new PageDto<>(p.getContent().stream().map(mapper).toList(),
        p.getNumber(), p.getSize(), p.getTotalElements());
  }
}
