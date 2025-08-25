package io.github.flowrapp.port.output;

import io.github.flowrapp.model.Mail;

import org.jspecify.annotations.NonNull;

public interface MailSenderPort {

  /**
   * Sends the given mail asynchronously. The method returns immediately, and the mail is sent in the background.
   */
  void sendAsync(@NonNull Mail mail);

}
