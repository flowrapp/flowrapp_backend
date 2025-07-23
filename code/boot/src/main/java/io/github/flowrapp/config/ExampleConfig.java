package io.github.flowrapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ExampleConfig {

  @Bean
  @Primary
  ObjectMapper jacksonMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .registerModule(new BlackbirdModule());
  }

}
