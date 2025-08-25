package io.github.flowrapp.port.output;

import io.github.flowrapp.model.Mail;

public interface MailSenderPort {

  void send(Mail mail);

}
