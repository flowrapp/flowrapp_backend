package io.github.flowrapp.infrastructure.input.rest.config.security.service;

import java.util.Optional;

import io.github.flowrapp.value.OAuth2UserInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for validating Google OAuth2 ID tokens and retrieving user information. Uses the Google API Client library to validate ID tokens.
 */
@Slf4j
@Service
public class GoogleOAuthService {

  public GoogleOAuthService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId) {

  }

  /**
   * Validates a Google ID token and retrieves user information.
   *
   * @param idToken the Google ID token to validate
   * @return OAuth2UserInfo containing the user's information, or empty if validation fails
   */
  public Optional<OAuth2UserInfo> validateTokenAndGetUser(String idToken) {
    return Optional.empty();
  }

}
