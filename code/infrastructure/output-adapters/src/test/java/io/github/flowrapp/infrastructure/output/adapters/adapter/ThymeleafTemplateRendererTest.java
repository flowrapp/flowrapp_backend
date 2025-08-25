package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ThymeleafTemplateRendererTest {

  @Mock
  private TemplateEngine templateEngine;

  @InjectMocks
  private ThymeleafTemplateRenderer thymeleafTemplateRenderer;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void render(String path, Map<String, Object> variables) {
    assertDoesNotThrow(() -> thymeleafTemplateRenderer.render(path, variables));
  }
}
