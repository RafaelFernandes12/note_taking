package com.rafael.notes_projeto.files.dto;

import org.springframework.web.multipart.MultipartFile;

import com.rafael.notes_projeto.files.model.Files;

public record FilesRequest(
    MultipartFile file) {

  public Files toEntity() {
    return Files.builder()
        .build();
  }

  public void applyTo(Files files) {
    // TODO: copiar os campos para a entidade, ex.: files.setNome(nome());
  }
}
