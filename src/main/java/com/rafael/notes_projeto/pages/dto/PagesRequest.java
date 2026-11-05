package com.rafael.notes_projeto.pages.dto;

import java.util.Set;

import com.rafael.notes_projeto.pages.model.Pages;
import com.rafael.notes_projeto.pages.model.TiptapNode;
import com.rafael.notes_projeto.tags.model.Tags;

public record PagesRequest(
    TiptapNode content,
    Set<Tags> tags,
    Set<Pages> links,
    String title,
    String searchText

) {

  public Pages toEntity() {
    return Pages.builder()
        .content(content())
        .tags(tags())
        .links(links())
        .title(title())
        .searchText(searchText())
        .build();
  }

  public void applyTo(Pages page) {
    page.setContent(content());
    page.setTitle(title());
    page.setSearchText(searchText());
    page.setTags(tags());
    page.setLinks(links());
  }
}
