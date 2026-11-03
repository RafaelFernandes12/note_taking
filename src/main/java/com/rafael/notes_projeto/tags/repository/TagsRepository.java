package com.rafael.notes_projeto.tags.repository;

import com.rafael.notes_projeto.tags.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
}
