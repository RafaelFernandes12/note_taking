package com.rafael.notes_projeto.tags.dto;

import com.rafael.notes_projeto.tags.model.Tags;

public record TagsRequest(
    String name) {

  public Tags toEntity() {
    return Tags.builder()
        .name(name())
        .build();
  }

  public void applyTo(Tags tags) {
    tags.setName(name());
  }
}
