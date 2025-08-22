package io.github.flowrapp.infrastructure.input.rest.config.security.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import io.github.flowrapp.config.JwtTokenSettings;
import io.github.flowrapp.infrastructure.input.rest.config.security.value.ClaimConstants;
import io.github.flowrapp.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Service for JWT token operations, including creation and parsing.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtTokenService {

  private static final String DEFAULT_AUTHORITY_CLAIM_NAME = "scope";

  private final JwtEncoder jwtAccessEncoder;

  private final JwtDecoder jwtAccessDecoder;

  private final JwtEncoder jwtRefreshEncoder;

  private final @Qualifier("jwtRefreshDecoder") JwtDecoder jwtRefreshDecoder;

  private final JwtTokenSettings jwtTokenSettings;

  /** Creates an access token for a user. */
  public @NonNull String createAccessToken(@NonNull User user) {
    return createToken(jwtAccessEncoder, user, jwtTokenSettings.getAccessToken().getExpirationTime());
  }

  /**
   * Creates a refresh token for a user.
   *
   * @param user The user for whom to create the token
   * @return The refresh token string
   */
  public @NonNull String createRefreshToken(@NonNull User user) {
    return createToken(jwtRefreshEncoder, user, jwtTokenSettings.getRefreshToken().getExpirationTime());
  }

  /** Decodes an access token. */
  public Optional<Jwt> decodeAccessToken(@NonNull String accessToken) {
    return this.tryAndDecodeToken(jwtAccessDecoder, accessToken);
  }

  /** Decodes a refresh token. */
  public Optional<Jwt> decodeRefreshToken(@NonNull String refreshToken) {
    return this.tryAndDecodeToken(jwtRefreshDecoder, refreshToken);
  }

  /** Creates a JWT token. */
  private String createToken(JwtEncoder jwtEncoder, User user, long expirationTime) {
    val now = Instant.now();
    val expiry = now.plus(expirationTime, ChronoUnit.SECONDS);

    val claims = JwtClaimsSet.builder()
        .issuedAt(now)
        .expiresAt(expiry)
        .subject(user.id().toString())
        .claim(ClaimConstants.CLAIM_KEY_USER_NAME, user.name())
        .claim(ClaimConstants.CLAIM_KEY_USER_MAIL, user.mail())
        .claim(DEFAULT_AUTHORITY_CLAIM_NAME,
            user.name().equalsIgnoreCase("admin") ? "ADMIN" : "USER")
        .build();

    val headers = JwtEncoderParameters.from(
        JwsHeader.with(MacAlgorithm.HS256).build(),
        claims);

    return jwtEncoder.encode(headers).getTokenValue();
  }

  private Optional<Jwt> tryAndDecodeToken(JwtDecoder jwtDecoder, @NonNull String token) {
    try {
      return Optional.of(jwtDecoder.decode(token));
    } catch (Exception e) {
      log.warn("Failed to decode token: {}", e.getMessage());
      return Optional.empty();
    }
  }

}
