package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.value.GoogleUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * Service for validating Google OAuth2 ID tokens and retrieving user information. Uses Google's tokeninfo endpoint to validate ID tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

  private final GoogleIdTokenVerifier verifier;

  /**
   * Validates a Google ID token against Google's public keys and retrieves user information.
   */
  @SneakyThrows
  public Optional<OAuth2UserInfo> validateTokenAndGetUser(String idToken) {
    var token = verifier.verify(idToken);
    if (token == null) {
      log.warn("Invalid ID token.");
      return Optional.empty();
    }

    val payload = token.getPayload();

    return Optional.of(
        GoogleUserInfo.builder()
            .id(payload.getSubject())
            .email(payload.getEmail())
            .name(this.getNameOrDefaultMail(payload))
            .avatarUrl((String) payload.get("picture"))
            .provider("google")
            .build());
  }

  private String getNameOrDefaultMail(Payload payload) {
    var name = (String) payload.get("name");
    if (name == null || name.isBlank()) {
      name = payload.getEmail().split("@")[0];
    }
    return name;
  }

}
