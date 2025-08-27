package io.github.flowrapp.infrastructure.output.adapters.adapter;

import io.github.flowrapp.model.Mail;
import io.github.flowrapp.port.output.MailSenderPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
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
  public void send(@NonNull Mail mail) {
    log.debug("Sending html email to {}", mail.recipient());

    try {
      mailer.sendMail(
          EmailBuilder.startingBlank()
              .to(mail.recipient())
              .from(from)
              .withSubject(mail.subject())
              .withHTMLText(mail.body())
              .buildEmail());

      log.info("Email sent to {}", mail.recipient());

    } catch (Exception e) {
      log.error("Failed to send email to {}", mail.recipient(), e);
    }
  }

}
