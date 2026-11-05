package com.rafael.notes_projeto.pages.service;

import com.rafael.notes_projeto.pages.dto.PagesRequest;
import com.rafael.notes_projeto.pages.dto.PagesResponse;
import com.rafael.notes_projeto.pages.exception.PagesNotFoundException;
import com.rafael.notes_projeto.pages.model.Pages;
import com.rafael.notes_projeto.pages.repository.PagesRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagesService {

  private final PagesRepository pagesRepository;

  @Transactional(readOnly = true)
  public Page<PagesResponse> findAll(String title, Pageable pageable) {
    return pagesRepository.findByTitleContainingIgnoreCase(title, pageable).map(PagesResponse::from);
  }

  @Transactional(readOnly = true)
  public PagesResponse findById(Long id) {
    return PagesResponse.from(buscar(id));
  }

  @Transactional
  public PagesResponse create(PagesRequest request) {
    Pages page = request.toEntity();
    page.setSearchText(page.getContent().plainText());
    return PagesResponse.from(pagesRepository.save(page));
  }

  @Transactional
  public PagesResponse update(Long id, PagesRequest request) {
    Pages page = buscar(id);
    request.applyTo(page);

    page.setSearchText(page.getContent().plainText());
    return PagesResponse.from(pagesRepository.save(page));
  }

  @Transactional
  public void delete(Long id) {
    pagesRepository.delete(buscar(id));
  }

  private Pages buscar(Long id) {
    return pagesRepository.findById(id)
        .orElseThrow(() -> new PagesNotFoundException(id));
  }
}
