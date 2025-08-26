package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.value.GoogleUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for validating Google OAuth2 ID tokens and retrieving user information. Uses Google's tokeninfo endpoint to validate ID tokens.
 */
@Slf4j
@Service
public class GoogleOAuthService {

  private static final String GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=";

  private final String clientId;

  private final HttpClient httpClient;

  private final ObjectMapper objectMapper;

  public GoogleOAuthService(@Value("${app.security.oauth2.client.registration.google.client-id}") String clientId) {
    this.clientId = clientId;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Validates a Google ID token and retrieves user information.
   *
   * @param idToken the Google ID token to validate
   * @return OAuth2UserInfo containing the user's information, or empty if validation fails
   */
  public Optional<OAuth2UserInfo> validateTokenAndGetUser(String idToken) {
    try {
      log.debug("Validating Google ID token");

      // Call Google's tokeninfo endpoint
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(GOOGLE_TOKEN_INFO_URL + idToken))
          .timeout(Duration.ofSeconds(10))
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        log.warn("Google tokeninfo returned status code: {}", response.statusCode());
        return Optional.empty();
      }

      // Parse the response
      JsonNode jsonNode = objectMapper.readTree(response.body());

      // Verify the audience (client ID)
      String audience = jsonNode.get("aud").asText();
      if (!clientId.equals(audience)) {
        log.warn("Token audience mismatch. Expected: {}, Got: {}", clientId, audience);
        return Optional.empty();
      }

      // Verify the issuer
      String issuer = jsonNode.get("iss").asText();
      if (!"https://accounts.google.com".equals(issuer) && !"accounts.google.com".equals(issuer)) {
        log.warn("Token issuer mismatch. Got: {}", issuer);
        return Optional.empty();
      }

      // Extract user information
      String userId = jsonNode.get("sub").asText();
      String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
      String name = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
      String pictureUrl = jsonNode.has("picture") ? jsonNode.get("picture").asText() : null;

      // Handle cases where name might be null
      if (name == null || name.trim().isEmpty()) {
        String givenName = jsonNode.has("given_name") ? jsonNode.get("given_name").asText() : null;
        String familyName = jsonNode.has("family_name") ? jsonNode.get("family_name").asText() : null;
        if (givenName != null && familyName != null) {
          name = givenName + " " + familyName;
        } else if (givenName != null) {
          name = givenName;
        } else {
          name = "Google User";
        }
      }

      GoogleUserInfo userInfo = new GoogleUserInfo(
          userId,
          email,
          name,
          pictureUrl,
          "google");

      log.debug("Successfully validated Google ID token for user: {}", email);
      return Optional.of(userInfo);

    } catch (IOException | InterruptedException e) {
      log.error("Error calling Google tokeninfo endpoint: {}", e.getMessage());
      return Optional.empty();
    } catch (Exception e) {
      log.error("Unexpected error during Google ID token validation", e);
      return Optional.empty();
    }
  }

}
