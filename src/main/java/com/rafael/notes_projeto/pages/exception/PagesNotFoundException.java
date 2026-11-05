package com.rafael.notes_projeto.pages.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PagesNotFoundException extends RuntimeException {

    public PagesNotFoundException(Long id) {
        super("Pages com id " + id + " não encontrado(a)");
    }
}
