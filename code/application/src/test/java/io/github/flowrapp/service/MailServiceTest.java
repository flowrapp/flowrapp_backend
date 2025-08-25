package io.github.flowrapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.port.output.MailSenderPort;
import io.github.flowrapp.port.output.TemplateRenderPort;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** To be honest there is nothing to test here **/
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

    // When
    mailService.sendOwnerCreation(invitation, randomPassword);

    // Then
    verify(mailSenderPort).send(any());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void sendInvitationToRegister(Invitation invitation) {
    // Given

    // When
    mailService.sendInvitationToRegister(invitation);

    // Then
    verify(mailSenderPort).send(any());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void sendInvitationTo(Invitation invitation) {
    // Given

    // When
    mailService.sendInvitationTo(invitation);

    // Then
    verify(mailSenderPort).send(any());
  }
}
