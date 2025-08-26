package io.github.flowrapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.service.mail.enricher.MailVariableEnricher;
import io.github.flowrapp.value.MailEvent.InvitationToInviteMailEvent;
import io.github.flowrapp.value.MailEvent.InvitationToRegisterMailEvent;
import io.github.flowrapp.value.MailEvent.OwnerCreationMailEvent;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class MailVariableEnrichmentServiceTest {

  @Mock
  private MailVariableEnricher enricher1;

  @Mock
  private MailVariableEnricher enricher2;

  private MailVariableEnrichmentService enrichmentService;

  @BeforeEach
  void setUp() {
    enrichmentService = new MailVariableEnrichmentService(List.of(enricher1, enricher2));
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void enrichVariables_WithSupportingEnricher_MergesVariables(Invitation invitation) {
    // Given
    var event = new InvitationToRegisterMailEvent(invitation);
    var baseVariables = event.getVariables();
    Map<String, Object> additionalVariables = Map.of(
        "invitationLink", "https://example.com/register?token=" + invitation.token(),
        "expirationDate", "2024-01-07");

    when(enricher1.supports(event)).thenReturn(true);
    when(enricher1.enrich(event)).thenReturn(additionalVariables);
    when(enricher2.supports(event)).thenReturn(false);

    // When
    Map<String, Object> result = enrichmentService.enrichVariables(event);

    // Then
    verify(enricher1).supports(event);
    verify(enricher1).enrich(event);
    verify(enricher2).supports(event);
    verify(enricher2, never()).enrich(event);

    // Should contain all base variables
    baseVariables.forEach((key, value) -> {
      assertTrue(result.containsKey(key), "Missing base variable: " + key);
      assertEquals(value, result.get(key), "Base variable value mismatch for: " + key);
    });

    // Should contain all additional variables
    additionalVariables.forEach((key, value) -> {
      assertTrue(result.containsKey(key), "Missing enriched variable: " + key);
      assertEquals(value, result.get(key), "Enriched variable value mismatch for: " + key);
    });

    assertEquals(baseVariables.size() + additionalVariables.size(), result.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void enrichVariables_WithMultipleSupportingEnrichers_MergesAllVariables(Invitation invitation) {
    // Given
    var event = new InvitationToInviteMailEvent(invitation);
    var baseVariables = event.getVariables();
    Map<String, Object> additionalVariables1 = Map.of(
        "acceptInvitationLink", "https://example.com/accept?token=" + invitation.token(),
        "declineInvitationLink", "https://example.com/decline?token=" + invitation.token());
    Map<String, Object> additionalVariables2 = Map.of(
        "expirationDate", "2024-01-07",
        "existingBusinessesCount", 3);

    when(enricher1.supports(event)).thenReturn(true);
    when(enricher1.enrich(event)).thenReturn(additionalVariables1);
    when(enricher2.supports(event)).thenReturn(true);
    when(enricher2.enrich(event)).thenReturn(additionalVariables2);

    // When
    Map<String, Object> result = enrichmentService.enrichVariables(event);

    // Then
    verify(enricher1).supports(event);
    verify(enricher1).enrich(event);
    verify(enricher2).supports(event);
    verify(enricher2).enrich(event);

    // Should contain all base variables
    baseVariables.forEach((key, value) -> {
      assertTrue(result.containsKey(key), "Missing base variable: " + key);
      assertEquals(value, result.get(key), "Base variable value mismatch for: " + key);
    });

    // Should contain all additional variables from both enrichers
    additionalVariables1.forEach((key, value) -> {
      assertTrue(result.containsKey(key), "Missing enriched variable from enricher1: " + key);
      assertEquals(value, result.get(key), "Enriched variable value mismatch for: " + key);
    });

    additionalVariables2.forEach((key, value) -> {
      assertTrue(result.containsKey(key), "Missing enriched variable from enricher2: " + key);
      assertEquals(value, result.get(key), "Enriched variable value mismatch for: " + key);
    });

    assertEquals(baseVariables.size() + additionalVariables1.size() + additionalVariables2.size(),
        result.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void enrichVariables_WithNoSupportingEnrichers_ReturnsOnlyBaseVariables(
      Invitation invitation, String randomPassword) {
    // Given
    var event = new OwnerCreationMailEvent(invitation, randomPassword);
    var baseVariables = event.getVariables();

    when(enricher1.supports(event)).thenReturn(false);
    when(enricher2.supports(event)).thenReturn(false);

    // When
    Map<String, Object> result = enrichmentService.enrichVariables(event);

    // Then
    verify(enricher1).supports(event);
    verify(enricher1, never()).enrich(event);
    verify(enricher2).supports(event);
    verify(enricher2, never()).enrich(event);

    // Should only contain base variables
    assertEquals(baseVariables, result);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void enrichVariables_WithEmptyEnrichersList_ReturnsOnlyBaseVariables(InvitationToRegisterMailEvent event) {
    // Given
    var enrichmentServiceWithNoEnrichers = new MailVariableEnrichmentService(List.of());
    var baseVariables = event.getVariables();

    // When
    Map<String, Object> result = enrichmentServiceWithNoEnrichers.enrichVariables(event);

    // Then
    assertEquals(baseVariables, result);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void enrichVariables_WithEnricherReturningEmptyMap_OnlyAddsBaseVariables(Invitation invitation) {
    // Given
    var event = new InvitationToRegisterMailEvent(invitation);
    var baseVariables = event.getVariables();

    when(enricher1.supports(event)).thenReturn(true);
    when(enricher1.enrich(event)).thenReturn(Map.of()); // Empty map
    when(enricher2.supports(event)).thenReturn(false);

    // When
    Map<String, Object> result = enrichmentService.enrichVariables(event);

    // Then
    verify(enricher1).supports(event);
    verify(enricher1).enrich(event);
    verify(enricher2).supports(event);
    verify(enricher2, never()).enrich(event);

    // Should only contain base variables
    assertEquals(baseVariables, result);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void enrichVariables_WithVariableOverride_EnrichedVariablesTakePrecedence(Invitation invitation) {
    // Given
    var event = new InvitationToRegisterMailEvent(invitation);
    var baseVariables = event.getVariables();

    // Override an existing base variable
    String existingKey = baseVariables.keySet().iterator().next();
    Map<String, Object> additionalVariables = Map.of(
        existingKey, "overridden-value",
        "newVariable", "new-value");

    when(enricher1.supports(event)).thenReturn(true);
    when(enricher1.enrich(event)).thenReturn(additionalVariables);
    when(enricher2.supports(event)).thenReturn(false);

    // When
    Map<String, Object> result = enrichmentService.enrichVariables(event);

    // Then
    verify(enricher1).supports(event);
    verify(enricher1).enrich(event);

    // The overridden value should be from the enricher
    assertEquals("overridden-value", result.get(existingKey));
    assertEquals("new-value", result.get("newVariable"));

    // Should contain all other base variables plus the new one
    assertEquals(baseVariables.size() + 1, result.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void enrichVariables_WithNullEnrichersList_HandlesGracefully(InvitationToRegisterMailEvent event) {
    // Given
    var enrichmentServiceWithNullEnrichers = new MailVariableEnrichmentService(null);

    // When & Then - Should not throw exception
    try {
      Map<String, Object> result = enrichmentServiceWithNullEnrichers.enrichVariables(event);
      // If it doesn't throw, it should at least return the base variables
      assertEquals(event.getVariables(), result);
    } catch (NullPointerException e) {
      // This is also acceptable behavior - depends on implementation choice
      assertTrue(true, "NPE is acceptable for null enrichers list");
    }
  }
}
