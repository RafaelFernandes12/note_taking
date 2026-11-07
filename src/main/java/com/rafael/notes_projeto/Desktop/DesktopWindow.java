package com.rafael.notes_projeto.Desktop;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import me.friwi.jcefmaven.CefAppBuilder;

@Component
@RequiredArgsConstructor
class DesktopWindow {
  private final ConfigurableApplicationContext ctx;
  private final Environment env;

  @EventListener(ApplicationReadyEvent.class)
  public void open() throws Exception {
    String port = env.getProperty("local.server.port");
    CefAppBuilder builder = new CefAppBuilder();
    builder.setInstallDir(new File("jcef-bundle")); // baixa os nativos na 1ª execução
    // O Swing roda sob X11 (XWayland); sem isso o Chromium tenta usar Wayland
    // direto e não consegue se embutir na janela (fica tudo cinza).
    builder.addJcefArgs("--ozone-platform=x11");
    // Sem isso o Chromium abre a janela de "primeiro uso" (termos de serviço).
    builder.addJcefArgs("--no-first-run", "--no-default-browser-check");
    // Cache próprio do app, em vez do ~/.config/cef_user_data compartilhado.
    builder.getCefSettings().root_cache_path = new File("jcef-cache").getAbsolutePath();
    CefApp cef = builder.build();
    CefClient client = cef.createClient();
    // Em dev (profile "dev") a janela abre o Vite, com hot reload; sem ele,
    // abre o front já buildado que o próprio Spring serve.
    String url = env.getProperty("desktop.frontend-url", "http://127.0.0.1:" + port);
    CefBrowser browser = client.createBrowser(url, false, false);

    SwingUtilities.invokeLater(() -> {
      JFrame f = new JFrame("Notes");
      f.add(browser.getUIComponent());
      f.setSize(1280, 800);
      f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      f.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          cef.dispose();
          f.dispose();
          System.exit(SpringApplication.exit(ctx));
        }
      });
      f.setVisible(true);
    });
  }
}
