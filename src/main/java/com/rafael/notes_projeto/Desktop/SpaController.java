package com.rafael.notes_projeto.Desktop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * As rotas do front (react-router) existem só no navegador. Se a janela for
 * recarregada numa delas, o Spring devolve o index.html e o React resolve a rota.
 */
@Controller
class SpaController {

  @GetMapping({ "/", "/page/**", "/simple" })
  public String index() {
    return "forward:/index.html";
  }
}
