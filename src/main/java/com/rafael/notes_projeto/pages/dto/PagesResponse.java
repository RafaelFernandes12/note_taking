package com.rafael.notes_projeto.pages.dto;

import com.rafael.notes_projeto.pages.model.Pages;
import com.rafael.notes_projeto.pages.model.TiptapNode;
import com.rafael.notes_projeto.tags.model.Tags;

import java.time.LocalDateTime;
import java.util.Set;

public record PagesResponse(
    Long id,
    TiptapNode content,
    Set<Tags> tags,
    String title,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static PagesResponse from(Pages page) {
    return new PagesResponse(
        page.getId(),
        page.getContent(),
        page.getTags(),
        page.getTitle(),
        page.getCreatedAt(),
        page.getUpdatedAt());
  }
}
