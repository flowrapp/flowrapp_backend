package io.github.flowrapp.service.mail.enricher;

import java.util.Map;

import io.github.flowrapp.value.MailEvent;

/**
 * Interface for enriching mail template variables with dynamic content. Implementations can provide additional variables that depend on
 * runtime context, configuration, or external services.
 */
public interface MailVariableEnricher {

  /**
   * Enriches the mail event with additional dynamic variables.
   *
   * @param event the mail event to enrich
   * @return a map of additional variables to merge with the event's base variables
   */
  Map<String, Object> enrich(MailEvent event);

  /**
   * Determines if this enricher supports the given mail event type.
   *
   * @param event the mail event to check
   * @return true if this enricher can process the event, false otherwise
   */
  boolean supports(MailEvent event);
}
