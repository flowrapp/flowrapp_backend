package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.config.security.service.JwtTokenService;
import io.github.flowrapp.infrastructure.input.rest.config.security.value.ClaimConstants;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.value.TokensResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCryptoAdapter implements AuthCryptoPort {

  private final PasswordEncoder passwordEncoder;

  private final JwtTokenService jwtTokenService;

  @Override
  public String randomPassword() {
    // 16 chars, include letters, digits, and symbols
    return RandomStringUtils.secure().next(16, 33, 126, true, true);
  }

  @Override
  public String randomHashesPassword() {
    return this.hashPassword(this.randomPassword());
  }

  @Override
  public String hashPassword(String randomPassword) {
    return passwordEncoder.encode(randomPassword);
  }

  @Override
  public boolean checkPassword(@NonNull String rawPassword, @NonNull String passwordHash) {
    return passwordEncoder.matches(rawPassword, passwordHash);
  }

  @Override
  public @NonNull TokensResponse createTokens(@NonNull User user) {
    return TokensResponse.builder()
        .accessToken(jwtTokenService.createAccessToken(user))
        .refreshToken(jwtTokenService.createRefreshToken(user))
        .build();
  }

  @Override
  public Optional<String> getUserMailFromToken(@NonNull String refreshToken) {
    return jwtTokenService.decodeRefreshToken(refreshToken)
        .map(jwt -> jwt.getClaimAsString(ClaimConstants.CLAIM_KEY_USER_MAIL));
  }

}
