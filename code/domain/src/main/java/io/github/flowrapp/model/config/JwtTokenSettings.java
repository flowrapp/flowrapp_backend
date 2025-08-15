package io.github.flowrapp.model.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// This class is a placeholder for token configuration settings.
@Getter
@Setter
@Component
@ConfigurationProperties("app.security.jwt")
public class JwtTokenSettings {

  private TokenProperty accessToken;

  private TokenProperty refreshToken;

  @Data
  public static class TokenProperty {
    private final String secretKey;

    private final long expirationTime;

  }

}
