package io.github.flowrapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.flowrapp.service.mail.enricher.MailVariableEnricher;
import io.github.flowrapp.value.MailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for enriching mail template variables with dynamic content. This service coordinates multiple MailVariableEnricher
 * implementations to provide additional variables that depend on runtime context, configuration, or external services.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailVariableEnrichmentService {

  private final List<MailVariableEnricher> enrichers;

  /**
   * Enriches the mail event variables by merging base variables with dynamic variables from all applicable enrichers.
   *
   * @param event the mail event containing base variables
   * @return a map containing both base and enriched variables
   */
  public Map<String, Object> enrichVariables(MailEvent event) {
    log.debug("Enriching variables for mail event: {}", event.getClass().getSimpleName());

    // Start with base variables from the event
    var enrichedVariables = new HashMap<>(event.getVariables());

    // Apply enrichment from all supporting enrichers
    for (var enricher : enrichers) {
      if (enricher.supports(event)) {
        log.debug("Applying enricher: {} for event: {}",
            enricher.getClass().getSimpleName(), event.getClass().getSimpleName());

        var additionalVariables = enricher.enrich(event);
        enrichedVariables.putAll(additionalVariables);

        log.debug("Added {} additional variables from enricher {}",
            additionalVariables.size(), enricher.getClass().getSimpleName());
      }
    }

    log.debug("Final enriched variables count: {} for event: {}",
        enrichedVariables.size(), event.getClass().getSimpleName());

    return enrichedVariables;
  }
}
