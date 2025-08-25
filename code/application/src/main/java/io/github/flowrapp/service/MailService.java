package io.github.flowrapp.service;

import java.util.Map;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.Mail;
import io.github.flowrapp.model.MailTemplates;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.MailSenderPort;
import io.github.flowrapp.port.output.TemplateRenderPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final TemplateRenderPort templateRenderPort;

  private final MailSenderPort mailSenderPort;

  public void sendOwnerCreation(Invitation invitation, String randomPassword) {
    Map<String, Object> vars = Map.of(
        "username", this.safeNameOrMail(invitation.invited()),
        "password", randomPassword);

    mailSenderPort.sendAsync(
        this.createMail(invitation.invited().mail(), MailTemplates.OWNER_CREATED, vars));
  }

  public void sendInvitationToRegister(Invitation invitation) {
    Map<String, Object> vars = Map.of(
        "token", invitation.token().toString(),
        "username", this.safeNameOrMail(invitation.invited()),
        "businessName", invitation.business().name(),
        "role", invitation.role().name(),
        "invitedBy", this.safeNameOrMail(invitation.invitedBy()));

    mailSenderPort.sendAsync(
        this.createMail(invitation.invited().mail(), MailTemplates.INVITATION_REGISTER, vars));
  }

  public void sendInvitationTo(Invitation invitation) {
    Map<String, Object> vars = Map.of(
        "token", invitation.token().toString(),
        "username", this.safeNameOrMail(invitation.invited()),
        "businessName", invitation.business().name(),
        "role", invitation.role().name(),
        "invitedBy", this.safeNameOrMail(invitation.invitedBy()));

    mailSenderPort.sendAsync(
        this.createMail(invitation.invited().mail(), MailTemplates.INVITED_TO, vars));
  }

  private Mail createMail(String recipient, MailTemplates template, Map<String, Object> vars) {
    return Mail.builder()
        .subject(template.getSubject())
        .recipient(recipient)
        .body(templateRenderPort.render(template.getTemplate(), vars))
        .build();
  }

  private String safeNameOrMail(User user) {
    if (user == null) {
      return "";
    }

    var name = user.name();
    return (name != null && !name.isBlank()) ? name : user.mail();
  }

}
