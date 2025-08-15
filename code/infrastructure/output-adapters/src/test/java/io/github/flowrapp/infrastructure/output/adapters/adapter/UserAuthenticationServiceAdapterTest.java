package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.ClaimConstants;
import io.github.flowrapp.infrastructure.input.rest.mainapi.security.JwtTokenService;
import io.github.flowrapp.model.TokensResponse;
import io.github.flowrapp.model.User;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserAuthenticationServiceAdapterTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenService jwtTokenService;

  @Mock
  private Jwt jwt;

  @InjectMocks
  private UserAuthenticationServiceAdapter userAuthenticationServiceAdapter;

  @ParameterizedTest
  @InstancioSource
  void checkPassword_shouldReturnTrue_whenPasswordMatches(String rawPassword, String passwordHash) {
    // Given
    when(passwordEncoder.matches(rawPassword, passwordHash)).thenReturn(true);

    // When
    boolean result = userAuthenticationServiceAdapter.checkPassword(rawPassword, passwordHash);

    // Then
    assertThat(result).isTrue();
  }

  @ParameterizedTest
  @InstancioSource
  void checkPassword_shouldReturnFalse_whenPasswordDoesNotMatch(String rawPassword, String passwordHash) {
    // Given
    when(passwordEncoder.matches(rawPassword, passwordHash)).thenReturn(false);

    // When
    boolean result = userAuthenticationServiceAdapter.checkPassword(rawPassword, passwordHash);

    // Then
    assertThat(result).isFalse();
  }

  @ParameterizedTest
  @InstancioSource
  void createTokens_shouldReturnTokensResponse(User user) {
    // Given
    String accessToken = "access-token";
    String refreshToken = "refresh-token";
    when(jwtTokenService.createAccessToken(user)).thenReturn(accessToken);
    when(jwtTokenService.createRefreshToken(user)).thenReturn(refreshToken);

    // When
    TokensResponse result = userAuthenticationServiceAdapter.createTokens(user);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.accessToken()).isEqualTo(accessToken);
    assertThat(result.refreshToken()).isEqualTo(refreshToken);
  }

  @ParameterizedTest
  @InstancioSource
  void getUserMailFromToken_shouldReturnUserMail(String refreshToken, String userMail) {
    // Given
    when(jwtTokenService.decodeAccessToken(refreshToken)).thenReturn(Optional.of(jwt));
    when(jwt.getClaimAsString(ClaimConstants.CLAIM_KEY_USER_MAIL)).thenReturn(userMail);

    // When
    Optional<String> result = userAuthenticationServiceAdapter.getUserMailFromToken(refreshToken);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(userMail);
  }

  @ParameterizedTest
  @InstancioSource
  void getUserMailFromToken_shouldReturnEmpty_whenTokenCannotBeDecoded(String refreshToken) {
    // Given
    when(jwtTokenService.decodeAccessToken(refreshToken)).thenReturn(Optional.empty());

    // When
    Optional<String> result = userAuthenticationServiceAdapter.getUserMailFromToken(refreshToken);

    // Then
    assertThat(result).isEmpty();
  }
}
