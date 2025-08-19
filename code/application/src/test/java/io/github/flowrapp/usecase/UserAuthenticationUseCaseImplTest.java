package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.User;
import io.github.flowrapp.value.LoginRequest;
import io.github.flowrapp.value.RefreshRequest;
import io.github.flowrapp.value.TokensResponse;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.UserRepositoryOutput;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserAuthenticationUseCaseImplTest {

  @Mock
  private AuthCryptoPort authenticationServiceOutput;

  @Mock
  private UserRepositoryOutput userRepositoryOutput;

  @InjectMocks
  private UserAuthenticationUseCaseImpl userAuthenticationUseCase;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void retrieveUserByMail(LoginRequest loginRequest, User user) {
    // GIVEN
    when(userRepositoryOutput.findUserByEmail(loginRequest.username()))
        .thenReturn(Optional.of(user));

    // WHEN
    Optional<User> result = userAuthenticationUseCase.retrieveUserByMail(loginRequest.username());

    // THEN
    assertThat(result)
        .isNotNull()
        .isPresent()
        .get()
        .isEqualTo(user);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void loginUser(LoginRequest loginRequest, User user, TokensResponse tokensResponse) {
    // GIVEN
    when(userRepositoryOutput.findUserByEmail(loginRequest.username()))
        .thenReturn(Optional.of(user));
    when(authenticationServiceOutput.checkPassword(loginRequest.password(), user.passwordHash()))
        .thenReturn(true);
    when(authenticationServiceOutput.createTokens(user))
        .thenReturn(tokensResponse);

    // WHEN
    var result = userAuthenticationUseCase.loginUser(loginRequest);

    // THEN
    assertThat(result)
        .isNotNull()
        .isEqualTo(tokensResponse);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void loginUser_invalidCredentials(LoginRequest loginRequest) {
    // GIVEN
    when(userRepositoryOutput.findUserByEmail(loginRequest.username()))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> userAuthenticationUseCase.loginUser(loginRequest))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void refreshTokens(RefreshRequest refreshRequest, String mail, User user, TokensResponse tokensResponse) {
    // GIVEN
    when(authenticationServiceOutput.getUserMailFromToken(refreshRequest.refreshToken()))
        .thenReturn(Optional.of(mail));
    when(userRepositoryOutput.findUserByEmail(mail))
        .thenReturn(Optional.of(user));
    when(authenticationServiceOutput.createTokens(user))
        .thenReturn(tokensResponse);

    // WHEN
    TokensResponse result = userAuthenticationUseCase.refreshTokens(refreshRequest);

    // THEN
    assertThat(result)
        .isNotNull()
        .isEqualTo(tokensResponse);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void refreshTokens_invalidRefreshToken(RefreshRequest refreshRequest) {
    // GIVEN
    when(authenticationServiceOutput.getUserMailFromToken(refreshRequest.refreshToken()))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> userAuthenticationUseCase.refreshTokens(refreshRequest))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void refreshTokens_userNotFound(RefreshRequest refreshRequest, String mail) {
    // GIVEN
    when(authenticationServiceOutput.getUserMailFromToken(refreshRequest.refreshToken()))
        .thenReturn(Optional.of(mail));
    when(userRepositoryOutput.findUserByEmail(mail))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> userAuthenticationUseCase.refreshTokens(refreshRequest))
        .isInstanceOf(FunctionalException.class);
  }

}
