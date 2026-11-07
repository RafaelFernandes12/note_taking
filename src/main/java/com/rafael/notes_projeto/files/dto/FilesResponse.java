package com.rafael.notes_projeto.files.dto;

import com.rafael.notes_projeto.files.model.Files;
import java.time.LocalDateTime;

public record FilesResponse(
        Long id,
        // TODO: adicionar os campos de saída de Files
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static FilesResponse from(Files files) {
        return new FilesResponse(
                files.getId(),
                files.getCreatedAt(),
                files.getUpdatedAt()
        );
    }
}
