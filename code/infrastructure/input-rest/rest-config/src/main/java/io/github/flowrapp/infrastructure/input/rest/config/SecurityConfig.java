package io.github.flowrapp.infrastructure.input.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .formLogin(AbstractHttpConfigurer::disable) // No Form Login
        .logout(AbstractHttpConfigurer::disable) // No Logout
        .csrf(AbstractHttpConfigurer::disable) // No CSRF
        .cors(AbstractHttpConfigurer::disable) // No CORS
        .httpBasic(AbstractHttpConfigurer::disable) // Disable HTTP Basic Authentication (for now)
        // No Session pls
        .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .securityMatcher("/actuator/**", "/v3/api-docs/**") // Match Actuator and OpenAPI docs
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll() // Allow Actuator endpoints
            .requestMatchers("/v3/api-docs/**").permitAll()) // Allow OpenAPI docs
        .build();
  }

}
