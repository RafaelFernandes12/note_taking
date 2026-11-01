package com.rafael.notes_projeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class NotesProjetoApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(NotesProjetoApplication.class).headless(false).run(args);
  }

}
