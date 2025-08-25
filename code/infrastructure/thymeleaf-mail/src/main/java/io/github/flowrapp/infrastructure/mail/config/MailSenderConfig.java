package io.github.flowrapp.infrastructure.mail.config;

import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(SimpleJavaMailSpringSupport.class) // Import Simple Java Mail support
@Profile("!test") // Exclude from test profile
public class MailSenderConfig {
}
