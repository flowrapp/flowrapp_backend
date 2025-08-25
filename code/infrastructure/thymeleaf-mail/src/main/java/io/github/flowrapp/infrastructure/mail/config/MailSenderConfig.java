package io.github.flowrapp.infrastructure.mail.config;

import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SimpleJavaMailSpringSupport.class) // Import Simple Java Mail support
public class MailSenderConfig {
}
