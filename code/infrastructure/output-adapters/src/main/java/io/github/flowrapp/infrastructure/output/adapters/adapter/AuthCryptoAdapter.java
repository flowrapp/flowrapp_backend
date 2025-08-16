package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.ClaimConstants;
import io.github.flowrapp.infrastructure.input.rest.mainapi.security.JwtTokenService;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.TokensResponse;
import io.github.flowrapp.port.output.AuthCryptoPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCryptoAdapter implements AuthCryptoPort {

  private final PasswordEncoder passwordEncoder;

  private final JwtTokenService jwtTokenService;

  private final PasswordGenerator passwordGen = new PasswordGenerator();

  private final List<CharacterRule> rules = List.of(
      new CharacterRule(EnglishCharacterData.Digit),
      new CharacterRule(EnglishCharacterData.UpperCase),
      new CharacterRule(EnglishCharacterData.Alphabetical));

  @Override
  public String randomPassword() {
    return passwordGen.generatePassword(10, rules);
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
