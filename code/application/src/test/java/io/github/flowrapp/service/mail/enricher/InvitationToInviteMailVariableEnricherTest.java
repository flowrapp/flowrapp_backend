package io.github.flowrapp.service.mail.enricher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.flowrapp.value.MailEvent.InvitationToInviteMailEvent;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class InvitationToInviteMailVariableEnricherTest {

  private InvitationToInviteMailVariableEnricher enricher;

  @BeforeEach
  void setUp() {
    enricher = new InvitationToInviteMailVariableEnricher("http://localhost:8080", "/invitations");
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void enrich(InvitationToInviteMailEvent event) {
    // GIVEN

    // WHEN
    var result = enricher.enrich(event);

    // THEN
    var expectedUrl = String.format(
        "http://localhost:8080/invitations?username=%s&business=%s&role=%s&invitedBy=%s&token=%s",
        event.invitation().invited().getUserOrMail(),
        event.invitation().business().name(),
        event.invitation().role().name(),
        event.invitation().invitedBy().getUserOrMail(),
        event.invitation().token().toString());

    assertThat(result)
        .isNotNull()
        .containsEntry("invitationUrl", expectedUrl);
  }

  @Test
  void supports() {
    assertTrue(
        enricher.supports(
            new InvitationToInviteMailEvent(null)));
  }

}
