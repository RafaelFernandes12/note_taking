package com.rafael.notes_projeto.tags.service;

import com.rafael.notes_projeto.tags.dto.TagsRequest;
import com.rafael.notes_projeto.tags.dto.TagsResponse;
import com.rafael.notes_projeto.tags.exception.TagsNotFoundException;
import com.rafael.notes_projeto.tags.model.Tags;
import com.rafael.notes_projeto.tags.repository.TagsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagsService {

    private final TagsRepository tagsRepository;

    @Transactional(readOnly = true)
    public List<TagsResponse> findAll() {
        return tagsRepository.findAll().stream().map(TagsResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TagsResponse findById(Long id) {
        return TagsResponse.from(buscar(id));
    }

    @Transactional
    public TagsResponse create(TagsRequest request) {
        Tags tags = request.toEntity();
        return TagsResponse.from(tagsRepository.save(tags));
    }

    @Transactional
    public TagsResponse update(Long id, TagsRequest request) {
        Tags tags = buscar(id);
        request.applyTo(tags);
        return TagsResponse.from(tagsRepository.save(tags));
    }

    @Transactional
    public void delete(Long id) {
        tagsRepository.delete(buscar(id));
    }

    private Tags buscar(Long id) {
        return tagsRepository.findById(id)
                .orElseThrow(() -> new TagsNotFoundException(id));
    }
}
