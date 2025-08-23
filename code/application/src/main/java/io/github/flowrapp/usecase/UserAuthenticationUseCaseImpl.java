package io.github.flowrapp.usecase;

import java.util.Optional;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.input.UserAuthenticationUseCase;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.OAuthServiceOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.value.LoginRequest;
import io.github.flowrapp.value.OAuth2UserInfo;
import io.github.flowrapp.value.RefreshRequest;
import io.github.flowrapp.value.SensitiveInfo;
import io.github.flowrapp.value.TokensResponse;

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

  private final AuthCryptoPort authCryptoPort;

  private final OAuthServiceOutput oauthServiceOutput;

  @Override
  public Optional<User> retrieveUserByMail(@NonNull String email) {
    return userRepositoryOutput.findUserByEmail(email);
  }

  @Override
  public Optional<User> updateUserPasswordHash(String mail, @NonNull String password) {
    return userRepositoryOutput.findUserByEmail(mail)
        .map(user -> user.withPasswordHash(SensitiveInfo.of(password)))
        .map(userRepositoryOutput::save);
  }

  @Override
  public @NonNull TokensResponse loginUser(@NonNull LoginRequest request) {
    log.debug("Logging in user: {}", request.username());

    val user = userRepositoryOutput.findUserByEmail(request.username())
        .filter(u -> authCryptoPort.checkPassword(request.password(), u.passwordHash().get()))
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVALID_CREDENTIALS));

    log.debug("Creating access token for user: {}", user.mail());
    return authCryptoPort.createTokens(user);
  }

  @Override
  public @NonNull TokensResponse refreshTokens(@NonNull RefreshRequest request) {
    log.debug("Requested refreshing tokens for user: {}", request);

    val mail = authCryptoPort.getUserMailFromToken(request.refreshToken())
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVALID_REFRESH_TOKEN));

    val user = userRepositoryOutput.findUserByEmail(mail)
        .orElseThrow(() -> new FunctionalException(FunctionalError.USER_NOT_FOUND));

    log.debug("Successfully refreshing token for user: {}", user.mail());
    return authCryptoPort.createTokens(user);
  }

  @Override
  public @NonNull TokensResponse loginOauth2User(@NonNull String code, OAuth2UserInfo.Provider provider) {
    log.debug("OAuth2 login for user from provider: {}", provider);

    val oAuth2UserInfo = oauthServiceOutput.getUserFromToken(code, provider)
        .orElseThrow(() -> new FunctionalException(FunctionalError.OAUTH2_INVALID_TOKEN));

    val user = userRepositoryOutput.findUserByEmail(oAuth2UserInfo.getEmail())
        .orElseGet(() -> {
          log.debug("Creating new user from OAuth2 info: {}", oAuth2UserInfo);
          return userRepositoryOutput.save(
              User.fromOauth2Info(oAuth2UserInfo)); // Create new user if not exists
        });

    // Generate tokens for the user
    log.debug("Creating access token for OAuth2 user: {}", user.mail());
    return authCryptoPort.createTokens(user);
  }

}
