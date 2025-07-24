package io.github.flowrapp.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfig {

  @Bean
  Module javaTimeModule() {
    return new JavaTimeModule();
  }

  @Bean
  Module blackbirdModule() {
    return new BlackbirdModule();
  }

}
