package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.ClaimConstants;
import io.github.flowrapp.infrastructure.input.rest.mainapi.security.JwtTokenService;
import io.github.flowrapp.model.value.TokensResponse;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.UserAuthenticationServiceOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceAdapter implements UserAuthenticationServiceOutput {

  private final PasswordEncoder passwordEncoder;

  private final JwtTokenService jwtTokenService;

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
