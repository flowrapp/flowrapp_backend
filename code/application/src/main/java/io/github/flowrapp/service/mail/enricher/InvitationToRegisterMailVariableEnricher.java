package io.github.flowrapp.service.mail.enricher;

import java.util.Map;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.value.MailEvent;
import io.github.flowrapp.value.MailEvent.InvitationToRegisterMailEvent;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Enricher for InvitationToRegisterMailEvent that provides dynamic variables specific to user registration invitations.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvitationToRegisterMailVariableEnricher implements MailVariableEnricher {

  @Value("${app.frontend.url}")
  private final String frontBaseUrl;

  @Value("${app.frontend.paths.invitation-register:/invitationsRegister}")
  private final String invitationPath;

  @SneakyThrows
  @Override
  public Map<String, Object> enrich(MailEvent event) {
    if (!(event instanceof InvitationToRegisterMailEvent(Invitation invitation))) {
      return Map.of();
    }

    log.debug("Enriching InvitationToRegisterMailEvent");

    val invitationUrl = new URIBuilder(frontBaseUrl)
        .setPath(invitationPath)
        .addParameter("username", invitation.invited().getUserOrMail())
        .addParameter("mail", invitation.invited().mail())
        .addParameter("business", invitation.business().name())
        .addParameter("role", invitation.role().name())
        .addParameter("invitedBy", invitation.invitedBy().getUserOrMail())
        .addParameter("token", invitation.token().toString())
        .build().toString();

    return Map.of(
        MailVariableKeys.INVITATION_URL, invitationUrl);
  }

  @Override
  public boolean supports(MailEvent event) {
    return event instanceof InvitationToRegisterMailEvent;
  }

}
