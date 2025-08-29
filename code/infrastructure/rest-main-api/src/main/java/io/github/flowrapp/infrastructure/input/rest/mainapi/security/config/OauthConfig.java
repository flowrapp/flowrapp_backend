package io.github.flowrapp.infrastructure.input.rest.mainapi.security.config;

import java.time.Duration;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.GitHubRateLimitHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class OauthConfig {

  /**
   * Bean for verifying Google ID tokens using Google's public keys.
   */
  @Bean
  GoogleIdTokenVerifier googleApi(@Value("${app.security.oauth2.client.registration.google.client-id}") String clientId) {
    return new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(List.of(clientId))
            .setIssuers(List.of("https://accounts.google.com", "accounts.google.com"))
            .build();
  }

  @Bean
  @Scope("prototype")
  GitHubBuilder gitHubBuilder() {
    return new GitHubBuilder()
        .withRateLimitHandler(GitHubRateLimitHandler.WAIT);
  }

}
