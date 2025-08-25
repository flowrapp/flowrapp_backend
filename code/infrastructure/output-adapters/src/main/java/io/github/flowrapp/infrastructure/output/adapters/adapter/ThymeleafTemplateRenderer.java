package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static java.util.Objects.requireNonNullElse;

import java.util.Locale;
import java.util.Map;

import io.github.flowrapp.port.output.TemplateRenderPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThymeleafTemplateRenderer implements TemplateRenderPort {

  private final TemplateEngine templateEngine;

  @Override
  public String render(String path, Map<String, Object> variables, Locale locale) {
    log.debug("Rendering template {} with vars {}", path, variables == null ? 0 : variables.keySet());
    var context = new Context(locale, requireNonNullElse(variables, Map.of()));
    return templateEngine.process(path, context);
  }

}
