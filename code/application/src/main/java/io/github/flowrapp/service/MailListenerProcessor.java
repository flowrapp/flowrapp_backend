package io.github.flowrapp.service;

import java.util.Map;

import io.github.flowrapp.model.Mail;
import io.github.flowrapp.model.MailTemplates;
import io.github.flowrapp.port.output.MailSenderPort;
import io.github.flowrapp.port.output.TemplateRenderPort;
import io.github.flowrapp.value.MailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailListenerProcessor {

  private final TemplateRenderPort templateRenderPort;

  private final MailSenderPort mailSenderPort;

  @Async("mailEventExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void listenToMailEvent(MailEvent event) {
    log.debug("Processing mail event: type={}, template={}",
        event.getClass().getSimpleName(), event.getTemplate());

    mailSenderPort.send(
        this.createMail(
            event.recipient(),
            event.getTemplate(),
            event.getVariables()));
  }

  private Mail createMail(String recipient, MailTemplates template, Map<String, Object> vars) {
    return Mail.builder()
        .subject(template.getSubject())
        .recipient(recipient)
        .body(
            templateRenderPort.render(template.getTemplate(), vars))
        .build();
  }

}
