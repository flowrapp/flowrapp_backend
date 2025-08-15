package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.TokensResponse;

import org.jspecify.annotations.NonNull;

/**
 * Output port for user authentication service, defining methods for password checking, token creation, and token claims retrieval.
 */
public interface UserAuthenticationServiceOutput {

  /** Check if the provided raw password matches the stored password hash */
  boolean checkPassword(@NonNull String rawPassword, @NonNull String passwordHash);

  /** Create access and refresh tokens for the user */
  @NonNull
  TokensResponse createTokens(@NonNull User user);

  /** Retrieves the mail from the refresh token claims */
  Optional<String> getUserMailFromToken(@NonNull String refreshToken);

}
