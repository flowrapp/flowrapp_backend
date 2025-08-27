package io.github.flowrapp.service.mail.enricher;

import java.util.Map;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.value.MailEvent;
import io.github.flowrapp.value.MailEvent.InvitationToInviteMailEvent;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Enricher for InvitationToInviteMailEvent that provides dynamic variables specific to invitations for existing users.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvitationToInviteMailVariableEnricher implements MailVariableEnricher {

  @Value("${app.frontend.url}")
  private final String frontBaseUrl;

  @Value("${app.frontend.paths.invitation:/invitations}")
  private final String invitationPath;

  @SneakyThrows
  @Override
  public Map<String, Object> enrich(MailEvent event) {
    if (!(event instanceof InvitationToInviteMailEvent(Invitation invitation))) {
      return Map.of();
    }

    log.debug("Enriching variables for InvitationToInviteMailEvent with token: {}",
        invitation.token());

    val invitationUrl = new URIBuilder(frontBaseUrl)
        .setPath(invitationPath)
        .addParameter("username", invitation.invited().getUserOrMail())
        .addParameter("business", invitation.business().name())
        .addParameter("role", invitation.role().name())
        .addParameter("invitedBy", invitation.invitedBy().getUserOrMail())
        .addParameter("token", invitation.token().toString())
        .build().toString();

    return Map.of(
        "invitationLink", invitationUrl);
  }

  @Override
  public boolean supports(MailEvent event) {
    return event instanceof InvitationToInviteMailEvent;
  }
}
