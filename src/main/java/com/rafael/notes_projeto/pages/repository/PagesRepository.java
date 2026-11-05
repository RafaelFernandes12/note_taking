package com.rafael.notes_projeto.pages.repository;

import com.rafael.notes_projeto.pages.model.Pages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagesRepository extends JpaRepository<Pages, Long> {
  Page<Pages> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
