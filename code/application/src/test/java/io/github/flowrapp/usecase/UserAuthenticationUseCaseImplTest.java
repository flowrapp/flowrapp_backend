package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.OAuthServiceOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.value.LoginRequest;
import io.github.flowrapp.value.OAuth2UserInfo;
import io.github.flowrapp.value.OAuth2UserInfo.Provider;
import io.github.flowrapp.value.RefreshRequest;
import io.github.flowrapp.value.TokensResponse;

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
  private AuthCryptoPort authCryptoPort;

  @Mock
  private UserRepositoryOutput userRepositoryOutput;

  @Mock
  private OAuthServiceOutput oauthServiceOutput;

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
    when(authCryptoPort.checkPassword(loginRequest.password(), user.passwordHash().get()))
        .thenReturn(true);
    when(authCryptoPort.createTokens(user))
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
    when(authCryptoPort.getUserMailFromToken(refreshRequest.refreshToken()))
        .thenReturn(Optional.of(mail));
    when(userRepositoryOutput.findUserByEmail(mail))
        .thenReturn(Optional.of(user));
    when(authCryptoPort.createTokens(user))
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
    when(authCryptoPort.getUserMailFromToken(refreshRequest.refreshToken()))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> userAuthenticationUseCase.refreshTokens(refreshRequest))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void refreshTokens_userNotFound(RefreshRequest refreshRequest, String mail) {
    // GIVEN
    when(authCryptoPort.getUserMailFromToken(refreshRequest.refreshToken()))
        .thenReturn(Optional.of(mail));
    when(userRepositoryOutput.findUserByEmail(mail))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> userAuthenticationUseCase.refreshTokens(refreshRequest))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void loginOauth2User_success(String code, Provider provider, String mail,
      User user, TokensResponse tokensResponse) {

    // GIVEN
    var oAuth2UserInfo = mock(OAuth2UserInfo.class);
    when(oAuth2UserInfo.getEmail()).thenReturn(mail);
    when(oauthServiceOutput.getUserFromToken(code, provider))
        .thenReturn(Optional.of(oAuth2UserInfo));
    when(userRepositoryOutput.findUserByEmail(oAuth2UserInfo.getEmail()))
        .thenReturn(Optional.of(user));
    when(authCryptoPort.createTokens(user))
        .thenReturn(tokensResponse);

    // WHEN
    var result = userAuthenticationUseCase.loginOauth2User(code, provider);

    assertThat(result).isEqualTo(tokensResponse);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void loginOauth2User_userNotExists_success(String code, Provider provider, String mail,
      User user, TokensResponse tokensResponse) {

    // GIVEN
    var oAuth2UserInfo = mock(OAuth2UserInfo.class);
    when(oAuth2UserInfo.getEmail()).thenReturn(mail);
    when(oauthServiceOutput.getUserFromToken(code, provider))
        .thenReturn(Optional.of(oAuth2UserInfo));
    when(userRepositoryOutput.findUserByEmail(oAuth2UserInfo.getEmail()))
        .thenReturn(Optional.empty());
    when(userRepositoryOutput.save(argThat(argument -> argument.mail().equals(oAuth2UserInfo.getEmail()))))
        .thenReturn(user);
    when(authCryptoPort.createTokens(user))
        .thenReturn(tokensResponse);

    // WHEN
    var result = userAuthenticationUseCase.loginOauth2User(code, provider);

    assertThat(result).isEqualTo(tokensResponse);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void loginOauth2User_userNotExists_failure(String code, Provider provider, String mail) {
    // GIVEN
    when(oauthServiceOutput.getUserFromToken(code, provider))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> userAuthenticationUseCase.loginOauth2User(code, provider))
        .isInstanceOf(FunctionalException.class);
  }

}
