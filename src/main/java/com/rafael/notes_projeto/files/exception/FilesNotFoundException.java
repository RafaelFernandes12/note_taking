package com.rafael.notes_projeto.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FilesNotFoundException extends RuntimeException {

    public FilesNotFoundException(Long id) {
        super("Files com id " + id + " não encontrado(a)");
    }
}
