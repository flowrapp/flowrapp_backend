package io.github.flowrapp.port.output;

import java.util.Map;

public interface TemplateRenderPort {

  String render(String path, Map<String, Object> variables);

}
