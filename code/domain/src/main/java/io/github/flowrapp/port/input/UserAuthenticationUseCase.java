package io.github.flowrapp.port.input;

import java.util.Optional;

import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.LoginRequest;
import io.github.flowrapp.model.value.RefreshRequest;
import io.github.flowrapp.model.value.TokensResponse;

import org.jspecify.annotations.NonNull;

public interface UserAuthenticationUseCase {

  Optional<User> retrieveUserByMail(@NonNull String email);

  @NonNull
  TokensResponse loginUser(@NonNull LoginRequest request);

  @NonNull
  TokensResponse refreshTokens(@NonNull RefreshRequest request);

}
