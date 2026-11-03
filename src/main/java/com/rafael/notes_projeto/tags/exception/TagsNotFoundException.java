package com.rafael.notes_projeto.tags.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TagsNotFoundException extends RuntimeException {

    public TagsNotFoundException(Long id) {
        super("Tags com id " + id + " não encontrado(a)");
    }
}
