package com.rafael.notes_projeto.Desktop;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
    // Só para depurar: -Dspring-boot.run.arguments=--desktop.debug-port=9222
    // (expõe o navegador a qualquer processo local, não deixe ligado).
    String debugPort = env.getProperty("desktop.debug-port");
    if (debugPort != null) {
      builder.addJcefArgs("--remote-debugging-port=" + debugPort);
    }
    // Cache próprio do app, em vez do ~/.config/cef_user_data compartilhado.
    builder.getCefSettings().root_cache_path = new File("jcef-cache").getAbsolutePath();
    // Modo offscreen (OSR): o Chromium desenha dentro de um componente Swing e o
    // Java repassa mouse/teclado. No modo janela nativa o Chromium cria uma janela
    // X11 filha que, sob Wayland (XWayland), não recebe cliques.
    boolean offscreen = env.getProperty("desktop.offscreen", Boolean.class, true);
    builder.getCefSettings().windowless_rendering_enabled = offscreen;
    CefApp cef = builder.build();
    CefClient client = cef.createClient();
    // Em dev (profile "dev") a janela abre o Vite, com hot reload; sem ele,
    // abre o front já buildado que o próprio Spring serve.
    String url = env.getProperty("desktop.frontend-url", "http://127.0.0.1:" + port);
    CefBrowser browser = client.createBrowser(url, offscreen, false);
    if (offscreen) {
      // natural-scroll: conteúdo acompanha os dedos (como no touchpad do sistema).
      int direction = env.getProperty("desktop.natural-scroll", Boolean.class, true) ? -1 : 1;
      fixScroll(browser.getUIComponent(), env.getProperty("desktop.scroll-factor", Integer.class, 30), direction);
    }

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

  /**
   * No modo offscreen o JCEF repassa ao Chromium o {@code getUnitsToScroll()} do
   * Swing (normalmente 3) como se fossem pixels, então cada passo da roda ou do
   * touchpad rola só 3px. Aqui multiplicamos o evento antes de entregá-lo ao JCEF.
   * Pelo XWayland o Java também não recebe a "rolagem natural" do sistema, então
   * {@code direction = -1} inverte o sentido.
   */
  private static void fixScroll(java.awt.Component ui, int factor, int direction) {
    MouseWheelListener[] jcefListeners = ui.getMouseWheelListeners();
    for (MouseWheelListener l : jcefListeners) {
      ui.removeMouseWheelListener(l);
    }
    ui.addMouseWheelListener(e -> {
      MouseWheelEvent scaled = new MouseWheelEvent(e.getComponent(), e.getID(), e.getWhen(),
          e.getModifiersEx(), e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(),
          e.getClickCount(), e.isPopupTrigger(), e.getScrollType(),
          e.getScrollAmount() * factor, e.getWheelRotation() * direction,
          e.getPreciseWheelRotation() * direction);
      for (MouseWheelListener l : jcefListeners) {
        l.mouseWheelMoved(scaled);
      }
    });
  }
}
