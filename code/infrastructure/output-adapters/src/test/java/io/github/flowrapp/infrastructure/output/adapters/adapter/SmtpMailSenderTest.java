package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import io.github.flowrapp.model.Mail;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.simplejavamail.api.mailer.Mailer;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class SmtpMailSenderTest {

  private Mailer mailer;

  private SmtpMailSender smtpMailSender;

  @BeforeEach
  void setUp() {
    mailer = mock(Mailer.class);
    var from = "address";
    smtpMailSender = new SmtpMailSender(mailer, from);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void send(Mail mail) {
    // GIVEN
    when(mailer.sendMail(any(), eq(true)))
        .thenReturn(CompletableFuture.completedFuture(null));

    // WHEN + THEN
    assertDoesNotThrow(() -> smtpMailSender.send(mail));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void send_Error(Mail mail) {
    // GIVEN
    when(mailer.sendMail(any(), eq(true)))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Simulated failure")));

    // WHEN + THEN
    assertDoesNotThrow(() -> smtpMailSender.send(mail));
  }

}
