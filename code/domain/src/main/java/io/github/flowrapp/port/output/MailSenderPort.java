package io.github.flowrapp.port.output;

import io.github.flowrapp.model.Mail;

import org.jspecify.annotations.NonNull;

public interface MailSenderPort {

  /**
   * Sends the given mail.
   */
  void send(@NonNull Mail mail);

}
