package io.github.flowrapp.infrastructure.output.adapters.adapter;

import io.github.flowrapp.model.Mail;
import io.github.flowrapp.port.output.MailSenderPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpMailSender implements MailSenderPort {

  private final Mailer mailer;

  @Value("${simplejavamail.smtp.username}")
  private String from;

  @Override
  public void send(Mail mail) {
    log.debug("Sending html email to {}", mail.recipient());

    this.sendMailAsync(
        org.simplejavamail.email.EmailBuilder.startingBlank()
            .to(mail.recipient())
            .from(from)
            .withSubject(mail.subject())
            .withHTMLText(mail.body())
            .buildEmail());
  }

  private void sendMailAsync(Email mail) {
    mailer.sendMail(mail, true)
        .whenComplete((unused, throwable) -> {
          if (throwable != null) {
            log.error("Failed to send email to {}: {}", mail.getToRecipients(), throwable.getMessage());
          } else {
            log.info("Email sent successfully to {}", mail.getToRecipients());
          }
        });
  }

}
