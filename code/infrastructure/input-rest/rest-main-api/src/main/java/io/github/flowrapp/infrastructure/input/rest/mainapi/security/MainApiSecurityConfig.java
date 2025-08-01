package io.github.flowrapp.infrastructure.input.rest.mainapi.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MainApiSecurityConfig {

  @Bean
  public SecurityFilterChain mainApisecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .formLogin(AbstractHttpConfigurer::disable) // No Form Login
        .logout(AbstractHttpConfigurer::disable) // No Logout
        .csrf(AbstractHttpConfigurer::disable) // No CSRF
        .cors(AbstractHttpConfigurer::disable) // No CORS
        .httpBasic(withDefaults()) // Enable HTTP Basic Authentication
        // No Session pls
        .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/ping").permitAll()
            .requestMatchers("/api/**").authenticated())
        .build();
  }

}
