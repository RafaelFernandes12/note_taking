package com.rafael.notes_projeto.pages.controller;

import com.rafael.notes_projeto.pages.dto.PagesRequest;
import com.rafael.notes_projeto.pages.dto.PagesResponse;
import com.rafael.notes_projeto.pages.service.PagesService;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PagesController {

  private final PagesService pagesService;

  @GetMapping
  public PagedModel<PagesResponse> findAll(
      @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam String title) {

    return new PagedModel<>(pagesService.findAll(title, pageable));
  }

  @GetMapping("/{id}")
  public PagesResponse findById(@PathVariable Long id) {
    return pagesService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PagesResponse create(@RequestBody PagesRequest request) {
    return pagesService.create(request);
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PutMapping("/{id}")
  public PagesResponse update(@PathVariable Long id, @RequestBody PagesRequest request) {
    return pagesService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    pagesService.delete(id);
  }
}
