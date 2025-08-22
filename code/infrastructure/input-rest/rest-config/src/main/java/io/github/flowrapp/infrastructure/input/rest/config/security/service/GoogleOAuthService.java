package io.github.flowrapp.infrastructure.input.rest.config.security.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.config.security.value.GoogleUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for validating Google OAuth2 ID tokens and retrieving user information. Uses the Google API Client library to validate ID tokens.
 */
@Service
public class GoogleOAuthService {

  private static final Logger logger = LoggerFactory.getLogger(GoogleOAuthService.class);

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  private final GoogleIdTokenVerifier verifier;

  public GoogleOAuthService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId) {
    this.clientId = clientId;
    this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
        .setAudience(Collections.singletonList(clientId))
        .build();
  }

  /**
   * Validates a Google ID token and retrieves user information.
   *
   * @param idToken the Google ID token to validate
   * @return OAuth2UserInfo containing the user's information, or empty if validation fails
   */
  public Optional<OAuth2UserInfo> validateTokenAndGetUser(String idToken) {
    try {
      // Verify the ID token
      GoogleIdToken googleIdToken = verifier.verify(idToken);

      if (googleIdToken == null) {
        logger.warn("Google ID token verification failed - token is invalid");
        return Optional.empty();
      }

      // Extract payload from the verified token
      GoogleIdToken.Payload payload = googleIdToken.getPayload();

      // Create attributes map from the payload
      Map<String, Object> attributes = new HashMap<>();
      attributes.put("sub", payload.getSubject());
      attributes.put("email", payload.getEmail());
      attributes.put("name", payload.get("name"));
      attributes.put("given_name", payload.get("given_name"));
      attributes.put("family_name", payload.get("family_name"));
      attributes.put("picture", payload.get("picture"));

      // Create GoogleUserInfo from the attributes
      GoogleUserInfo userInfo = GoogleUserInfo.fromAttributes(attributes);

      logger.debug("Successfully validated Google ID token for user: {}", payload.getEmail());
      return Optional.of(userInfo);

    } catch (GeneralSecurityException e) {
      logger.warn("Google ID token verification failed due to security error: {}", e.getMessage());
      return Optional.empty();
    } catch (IOException e) {
      logger.warn("Google ID token verification failed due to IO error: {}", e.getMessage());
      return Optional.empty();
    } catch (Exception e) {
      logger.error("Unexpected error validating Google ID token", e);
      return Optional.empty();
    }
  }

}
