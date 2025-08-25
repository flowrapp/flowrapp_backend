package io.github.flowrapp.infrastructure.output.adapters.adapter;

import io.github.flowrapp.model.Mail;
import io.github.flowrapp.port.output.MailSenderPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SmtpMailSender implements MailSenderPort {

  private final Mailer mailer;

  @Value("${app.mail.from:${simplejavamail.smtp.username}}")
  private final String from;

  @Override
  public void sendAsync(@NonNull Mail mail) {
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
            log.error("Failed to send email to {}: {}", mail.getToRecipients().size(), throwable.getMessage());
          } else {
            log.info("Email sent successfully to {}", mail.getToRecipients().size());
          }
        });
  }

}
