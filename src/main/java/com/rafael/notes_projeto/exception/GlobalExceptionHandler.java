package com.rafael.notes_projeto.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @Value("${spring.servlet.multipart.max-file-size}")
  private String maxFileSize;

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ProblemDetail handleMaxUploadSize(MaxUploadSizeExceededException e) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.CONTENT_TOO_LARGE,
        "O arquivo excede o tamanho máximo permitido de " + maxFileSize + ".");
    problem.setTitle("Arquivo grande demais");
    return problem;
  }
}
