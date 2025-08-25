package io.github.flowrapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.Mail;
import io.github.flowrapp.model.MailTemplates;
import io.github.flowrapp.port.output.MailSenderPort;
import io.github.flowrapp.port.output.TemplateRenderPort;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class MailServiceTest {

  @Mock
  private TemplateRenderPort templateRenderPort;

  @Mock
  private MailSenderPort mailSenderPort;

  @InjectMocks
  private MailService mailService;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void sendOwnerCreation(Invitation invitation, String randomPassword) {
    // Given
    when(templateRenderPort.render(any(), anyMap())).thenReturn("rendered-body");

    // When
    mailService.sendOwnerCreation(invitation, randomPassword);

    // Then
    verify(mailSenderPort).sendAsync(any());
    verify(mailSenderPort).sendAsync(any());
    verify(templateRenderPort).render(eq(MailTemplates.OWNER_CREATED.getTemplate()), anyMap());
    var captor = ArgumentCaptor.forClass(Mail.class);
    verify(mailSenderPort).sendAsync(captor.capture());
    var sent = captor.getValue();
    assertEquals(MailTemplates.OWNER_CREATED.getSubject(), sent.subject());
    assertEquals("rendered-body", sent.body());
    assertEquals(invitation.invited().mail(), sent.recipient());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void sendInvitationToRegister(Invitation invitation) {
    // Given
    when(templateRenderPort.render(any(), anyMap())).thenReturn("rendered-body");

    // When
    mailService.sendInvitationToRegister(invitation);

    // Then
    verify(mailSenderPort).sendAsync(any());
    verify(templateRenderPort).render(eq(MailTemplates.INVITATION_REGISTER.getTemplate()), anyMap());
    var captor = ArgumentCaptor.forClass(Mail.class);
    verify(mailSenderPort).sendAsync(captor.capture());
    var sent = captor.getValue();
    assertEquals(io.github.flowrapp.model.MailTemplates.INVITATION_REGISTER.getSubject(), sent.subject());
    assertEquals("rendered-body", sent.body());
    assertEquals(invitation.invited().mail(), sent.recipient());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void sendInvitationTo(Invitation invitation) {
    // Given
    when(templateRenderPort.render(any(), anyMap())).thenReturn("rendered-body");

    // When
    mailService.sendInvitationTo(invitation);

    // Then
    verify(mailSenderPort).sendAsync(any());
    verify(templateRenderPort).render(eq(MailTemplates.INVITED_TO.getTemplate()), anyMap());
    var captor = ArgumentCaptor.forClass(Mail.class);
    verify(mailSenderPort).sendAsync(captor.capture());
    var sent = captor.getValue();
    assertEquals(MailTemplates.INVITED_TO.getSubject(), sent.subject());
    assertEquals("rendered-body", sent.body());
    assertEquals(invitation.invited().mail(), sent.recipient());
  }
}
