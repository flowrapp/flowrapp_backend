package io.github.flowrapp.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration(proxyBeanMethods = false)
@EnableAsync
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

  /** Executor for mail sending tasks */
  @Bean("mailEventExecutor")
  public ThreadPoolTaskExecutor mailEventExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2); // Always 2 threads ready
    executor.setMaxPoolSize(4); // Max 4 threads for mail operations
    executor.setQueueCapacity(50); // Queue up to 50 mail tasks
    executor.setKeepAliveSeconds(60); // Idle threads timeout
    executor.setThreadNamePrefix("MailEvent-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    return executor;
  }

}
