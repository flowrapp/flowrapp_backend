package io.github.flowrapp.infrastructure.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Import(SimpleJavaMailSpringSupport.class)
public class EmailSenderService {

  private final Mailer mailer;

  public void sendSimpleMessage(String to, String subject, String text) {
    log.debug("Sending email to " + to);

    final var mail = EmailBuilder.startingBlank()
        .to(to)
        .from("flowraapp@gmail.com")
        .withSubject(subject)
        .withPlainText(text)
        .buildEmail();

    mailer.sendMail(mail, true)
        .whenComplete((unused, throwable) -> {
          if (throwable != null) {
            log.error("Failed to send email to {}: {}", to, throwable.getMessage());
          } else {
            log.info("Email sent successfully to {}", to);
          }
        });

  }

}
