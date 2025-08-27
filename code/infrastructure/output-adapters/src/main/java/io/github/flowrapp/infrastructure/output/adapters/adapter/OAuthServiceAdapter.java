package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.service.GitHubOAuthService;
import io.github.flowrapp.infrastructure.input.rest.mainapi.security.service.GoogleOAuthService;
import io.github.flowrapp.port.output.OAuthServiceOutput;
import io.github.flowrapp.value.OAuth2UserInfo;
import io.github.flowrapp.value.OAuth2UserInfo.Provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceAdapter implements OAuthServiceOutput {

  private final GitHubOAuthService gitHubOAuthService;

  private final GoogleOAuthService googleOAuthService;

  @Override
  public Optional<OAuth2UserInfo> getUserFromToken(String credentials, Provider provider) {
    log.debug("Getting user from provider: {}", provider);

    return switch (provider) {
      case GITHUB -> gitHubOAuthService.validateTokenAndGetUser(credentials);
      case GOOGLE -> googleOAuthService.validateTokenAndGetUser(credentials);
    };
  }

}
