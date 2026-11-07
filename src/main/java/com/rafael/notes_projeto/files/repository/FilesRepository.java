package com.rafael.notes_projeto.files.repository;

import com.rafael.notes_projeto.files.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {
}
