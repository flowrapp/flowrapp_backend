package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.ClaimConstants;
import io.github.flowrapp.infrastructure.input.rest.mainapi.security.JwtTokenService;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.TokensResponse;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class AuthCryptoAdapterTest {

  @Spy
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 5);

  @Mock
  private JwtTokenService jwtTokenService;

  @InjectMocks
  private AuthCryptoAdapter authCryptoAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void randomPassword_shouldReturnRandomPassword() {
    // Given

    // When
    var generatedPassword = authCryptoAdapter.randomPassword();

    // Then
    assertThat(generatedPassword).isNotBlank();
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void randomHashesPassword_shouldReturnHashedPassword() {
    // Given

    // When
    var hashedPassword = authCryptoAdapter.randomHashesPassword();

    // Then
    assertThat(hashedPassword).isNotBlank();
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void hashPassword_shouldReturnHashedPassword(String randomPassword) {
    // Given

    // When
    var result = authCryptoAdapter.hashPassword(randomPassword);

    // Then
    assertThat(result)
        .isNotBlank();
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void checkPassword_shouldReturnTrue_whenPasswordMatches(String rawPassword) {
    // Given
    var passwordHash = passwordEncoder.encode(rawPassword);

    // When
    var result = authCryptoAdapter.checkPassword(rawPassword, passwordHash);

    // Then
    assertThat(result).isTrue();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void checkPassword_shouldReturnFalse_whenPasswordDoesNotMatch(String rawPassword, String passwordHash) {
    // Given

    // When
    boolean result = authCryptoAdapter.checkPassword(rawPassword, passwordHash);

    // Then
    assertThat(result).isFalse();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createTokens_shouldReturnTokensResponse(User user) {
    // Given
    String accessToken = "access-token";
    String refreshToken = "refresh-token";
    when(jwtTokenService.createAccessToken(user)).thenReturn(accessToken);
    when(jwtTokenService.createRefreshToken(user)).thenReturn(refreshToken);

    // When
    var result = authCryptoAdapter.createTokens(user);

    // Then
    assertThat(result)
        .isNotNull()
        .returns(accessToken, TokensResponse::accessToken)
        .returns(refreshToken, TokensResponse::refreshToken);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserMailFromToken_shouldReturnUserMail(String refreshToken, String userMail) {
    // Given
    var jwt = mock(Jwt.class);
    when(jwtTokenService.decodeRefreshToken(refreshToken))
        .thenReturn(Optional.of(jwt));
    when(jwt.getClaimAsString(ClaimConstants.CLAIM_KEY_USER_MAIL))
        .thenReturn(userMail);

    // When
    var result = authCryptoAdapter.getUserMailFromToken(refreshToken);

    // Then
    assertThat(result)
        .isPresent()
        .contains(userMail);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserMailFromToken_shouldReturnEmpty_whenTokenCannotBeDecoded(String refreshToken) {
    // Given

    // When
    var result = authCryptoAdapter.getUserMailFromToken(refreshToken);

    // Then
    assertThat(result).isEmpty();
  }
}
