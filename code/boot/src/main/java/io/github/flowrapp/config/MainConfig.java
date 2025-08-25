package io.github.flowrapp.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration(proxyBeanMethods = false)
@EnableAsync
@ImportRuntimeHints(ReportEntityRuntimeHints.ReportEntityRuntimeHintsRegistrar.class)
public class MainConfig {

  @Bean
  Module javaTimeModule() {
    return new JavaTimeModule();
  }

  @Bean
  Module blackbirdModule() {
    return new BlackbirdModule();
  }

  @Bean(name = "virtualThreadsExecutor")
  ExecutorService virtualThreads() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
