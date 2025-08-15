package io.github.flowrapp.usecase;

import java.util.Optional;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.value.LoginRequest;
import io.github.flowrapp.model.value.RefreshRequest;
import io.github.flowrapp.model.value.TokensResponse;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.input.UserAuthenticationUseCase;
import io.github.flowrapp.port.output.UserAuthenticationServiceOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationUseCaseImpl implements UserAuthenticationUseCase {

  private final UserRepositoryOutput userRepositoryOutput;

  private final UserAuthenticationServiceOutput userAuthenticationServiceOutput;

  @Override
  public Optional<User> retrieveUserByMail(@NonNull String email) {
    return userRepositoryOutput.findUserByEmail(email);
  }

  @Override
  public @NonNull TokensResponse loginUser(@NonNull LoginRequest request) {
    log.debug("Logging in user: {}", request.username());

    val user = userRepositoryOutput.findUserByEmail(request.username())
        .filter(u -> userAuthenticationServiceOutput.checkPassword(request.password(), u.passwordHash()))
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVALID_CREDENTIALS));

    log.debug("Creating access token for user: {}", user.mail());
    return userAuthenticationServiceOutput.createTokens(user);
  }

  @Override
  public @NonNull TokensResponse refreshTokens(@NonNull RefreshRequest request) {
    log.debug("Requested refreshing tokens for user: {}", request);

    val mail = userAuthenticationServiceOutput.getUserMailFromToken(request.refreshToken())
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVALID_REFRESH_TOKEN));

    val user = userRepositoryOutput.findUserByEmail(mail)
        .orElseThrow(() -> new FunctionalException(FunctionalError.USER_NOT_FOUND));

    log.debug("Successfully refreshing token for user: {}", user.mail());
    return userAuthenticationServiceOutput.createTokens(user);
  }

}
