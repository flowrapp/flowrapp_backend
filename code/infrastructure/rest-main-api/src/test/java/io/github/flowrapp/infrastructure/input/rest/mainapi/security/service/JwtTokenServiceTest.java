package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.github.flowrapp.config.JwtTokenSettings;
import io.github.flowrapp.config.JwtTokenSettings.TokenProperty;
import io.github.flowrapp.infrastructure.input.rest.mainapi.security.value.ClaimConstants;
import io.github.flowrapp.model.User;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtTokenServiceTest {

  @Mock
  private JwtEncoder jwtAccessEncoder;

  @Mock
  private JwtDecoder jwtAccessDecoder;

  @Mock
  private JwtEncoder jwtRefreshEncoder;

  @Mock
  private JwtDecoder jwtRefreshDecoder;

  @Mock
  private JwtTokenSettings jwtTokenSettings;

  @Mock
  private TokenProperty accessTokenProperty;

  @Mock
  private TokenProperty refreshTokenProperty;

  private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    jwtTokenService = new JwtTokenService(
        jwtAccessEncoder,
        jwtAccessDecoder,
        jwtRefreshEncoder,
        jwtRefreshDecoder,
        jwtTokenSettings);

    when(jwtTokenSettings.getAccessToken()).thenReturn(accessTokenProperty);
    when(jwtTokenSettings.getRefreshToken()).thenReturn(refreshTokenProperty);
    when(accessTokenProperty.getExpirationTime()).thenReturn(3600L);
    when(refreshTokenProperty.getExpirationTime()).thenReturn(86400L);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createAccessToken_shouldCreateToken(User user) {
    // Given
    String expectedToken = "access-token";
    Jwt jwt = Jwt.withTokenValue(expectedToken)
        .header("alg", "HS256")
        .claim("sub", user.id().toString())
        .claim(ClaimConstants.CLAIM_KEY_USER_NAME, user.name())
        .claim(ClaimConstants.CLAIM_KEY_USER_MAIL, user.mail())
        .build();

    when(jwtAccessEncoder.encode(any()))
        .thenReturn(jwt);

    // When
    String result = jwtTokenService.createAccessToken(user);

    // Then
    assertThat(result).isEqualTo(expectedToken);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createRefreshToken_shouldCreateToken(User user) {
    // Given
    String expectedToken = "refresh-token";
    org.springframework.security.oauth2.jwt.Jwt jwt = Jwt.withTokenValue(expectedToken)
        .header("alg", "HS256")
        .claim("sub", user.id().toString())
        .claim(ClaimConstants.CLAIM_KEY_USER_NAME, user.name())
        .claim(ClaimConstants.CLAIM_KEY_USER_MAIL, user.mail())
        .build();
    when(jwtRefreshEncoder.encode(any(JwtEncoderParameters.class)))
        .thenReturn(jwt);

    // When
    String result = jwtTokenService.createRefreshToken(user);

    // Then
    assertThat(result).isEqualTo(expectedToken);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void decodeAccessToken_shouldReturnJwt(User user) {
    // Given
    String token = "access-token";
    Jwt jwt = Jwt.withTokenValue(token)
        .header("alg", "HS256")
        .claim("sub", user.id().toString())
        .claim(ClaimConstants.CLAIM_KEY_USER_NAME, user.name())
        .claim(ClaimConstants.CLAIM_KEY_USER_MAIL, user.mail())
        .build();

    when(jwtAccessDecoder.decode(token)).thenReturn(jwt);

    // When
    var result = jwtTokenService.decodeAccessToken(token);

    // Then
    assertThat(result)
        .isPresent()
        .contains(jwt);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void decodeRefreshToken_shouldReturnJwt(User user) {
    // Given
    String token = "refresh-token";
    Jwt jwt = Jwt.withTokenValue(token)
        .header("alg", "HS256")
        .claim("sub", user.id().toString())
        .claim(ClaimConstants.CLAIM_KEY_USER_NAME, user.name())
        .claim(ClaimConstants.CLAIM_KEY_USER_MAIL, user.mail())
        .build();
    when(jwtRefreshDecoder.decode(token)).thenReturn(jwt);

    // When
    var result = jwtTokenService.decodeRefreshToken(token);

    // Then
    assertThat(result)
        .isPresent()
        .contains(jwt);
  }

  @Test
  void decodeAccessToken_shouldReturnEmptyWhenDecodeFails() {
    // Given
    String token = "invalid-token";
    when(jwtAccessDecoder.decode(token)).thenThrow(new RuntimeException("Invalid token"));

    // When
    var result = jwtTokenService.decodeAccessToken(token);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void decodeRefreshToken_shouldReturnEmptyWhenDecodeFails() {
    // Given
    String token = "invalid-token";
    when(jwtRefreshDecoder.decode(token)).thenThrow(new RuntimeException("Invalid token"));

    // When
    var result = jwtTokenService.decodeRefreshToken(token);

    // Then
    assertThat(result).isEmpty();
  }
}
