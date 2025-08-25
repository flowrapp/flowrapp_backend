package io.github.flowrapp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailTemplates {
  INVITED_TO("html/invitation-to.html", "You've been invited to a new business on Flowr"),
  INVITATION_REGISTER("html/invitation-register.html", "You're invited to join Flowr"),
  OWNER_CREATED("html/owner-created.html", "Welcome to Flowr"),
  ;

  private final String template;

  private final String subject;
}
