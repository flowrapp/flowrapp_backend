package io.github.flowrapp.value;

import java.util.Map;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.MailTemplates;

/**
 * Marker interface for mail events to be processed by the mail service.
 */
public interface MailEvent {

  MailTemplates getTemplate();

  String recipient();

  Map<String, Object> getVariables();

  /** Event to notify the creation of an owner user with its password. */
  record OwnerCreationMailEvent(Invitation invitation, String randomPassword) implements MailEvent {
    @Override
    public MailTemplates getTemplate() {
      return MailTemplates.OWNER_CREATED;
    }

    @Override
    public String recipient() {
      return invitation.invited().mail();
    }

    @Override
    public Map<String, Object> getVariables() {
      return Map.of(
          "username", invitation.invited().getUserOrMail(),
          "password", randomPassword);
    }
  }

  /** Event to notify an invitation to a user that was not in our systems. */
  record InvitationToRegisterMailEvent(Invitation invitation) implements MailEvent {
    @Override
    public MailTemplates getTemplate() {
      return MailTemplates.INVITATION_REGISTER;
    }

    @Override
    public String recipient() {
      return invitation.invited().mail();
    }

    @Override
    public Map<String, Object> getVariables() {
      return Map.of(
          "token", invitation.token().toString(),
          "username", invitation.invited().getUserOrMail(),
          "businessName", invitation.business().name(),
          "role", invitation.role().name(),
          "invitedBy", invitation.invitedBy().getUserOrMail());
    }
  }

  /** Event to notify an invitation to a user that already has an account. */
  record InvitationToInviteMailEvent(Invitation invitation) implements MailEvent {
    @Override
    public MailTemplates getTemplate() {
      return MailTemplates.INVITED_TO;
    }

    @Override
    public String recipient() {
      return invitation.invited().mail();
    }

    @Override
    public Map<String, Object> getVariables() {
      return Map.of(
          "token", invitation.token().toString(),
          "username", invitation.invited().getUserOrMail(),
          "businessName", invitation.business().name(),
          "role", invitation.role().name(),
          "invitedBy", invitation.invitedBy().getUserOrMail());
    }
  }

}
