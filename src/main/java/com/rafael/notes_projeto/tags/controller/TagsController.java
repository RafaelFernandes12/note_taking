package com.rafael.notes_projeto.tags.controller;

import com.rafael.notes_projeto.tags.dto.TagsRequest;
import com.rafael.notes_projeto.tags.dto.TagsResponse;
import com.rafael.notes_projeto.tags.service.TagsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagsController {

    private final TagsService tagsService;

    @GetMapping
    public List<TagsResponse> findAll() {
        return tagsService.findAll();
    }

    @GetMapping("/{id}")
    public TagsResponse findById(@PathVariable Long id) {
        return tagsService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagsResponse create(@RequestBody TagsRequest request) {
        return tagsService.create(request);
    }

    @PutMapping("/{id}")
    public TagsResponse update(@PathVariable Long id, @RequestBody TagsRequest request) {
        return tagsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tagsService.delete(id);
    }
}
