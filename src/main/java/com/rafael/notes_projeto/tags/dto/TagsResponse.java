package com.rafael.notes_projeto.tags.dto;

import com.rafael.notes_projeto.tags.model.Tags;
import java.time.LocalDateTime;

public record TagsResponse(
    Long id,
    String name,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static TagsResponse from(Tags tags) {
    return new TagsResponse(
        tags.getId(),
        tags.getName(),
        tags.getCreatedAt(),
        tags.getUpdatedAt());
  }
}
