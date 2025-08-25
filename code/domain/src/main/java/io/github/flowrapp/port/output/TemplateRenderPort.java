package io.github.flowrapp.port.output;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;

public interface TemplateRenderPort {

  /** Same as render(template, model, null) */
  default String render(String template, Map<String, Object> model) {
    return render(template, model, LocaleContextHolder.getLocale());
  }

  /**
   * Render a template located at the given path with the provided variables. Implementations should treat 'path' as a classpath-relative
   * resource and HTML-escape user-provided variables by default. Implementations should throw a domain-level TemplateRenderingException
   * (unchecked) when the template is missing or cannot be rendered. <p> Optional locale-aware variant to support i18n.
   */
  String render(String path, Map<String, Object> variables, Locale locale);

}
