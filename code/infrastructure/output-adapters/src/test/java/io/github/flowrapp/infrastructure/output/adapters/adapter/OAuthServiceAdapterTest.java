package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.config.security.service.GitHubOAuthService;
import io.github.flowrapp.infrastructure.input.rest.config.security.service.GoogleOAuthService;
import io.github.flowrapp.value.OAuth2UserInfo;
import io.github.flowrapp.value.OAuth2UserInfo.Provider;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({InstancioExtension.class, MockitoExtension.class})
class OAuthServiceAdapterTest {

  @Mock
  private GitHubOAuthService gitHubOAuthService;

  @Mock
  private GoogleOAuthService googleOAuthService;

  @InjectMocks
  private OAuthServiceAdapter oAuthServiceAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void getUserFromTest_whenIsGithub(String credentials) {
    // GIVEN
    var oauthUser = mock(OAuth2UserInfo.class);
    when(gitHubOAuthService.validateTokenAndGetUser(credentials))
        .thenReturn(Optional.of(oauthUser));

    // WHEN
    var result = oAuthServiceAdapter.getUserFromToken(credentials, Provider.GITHUB);

    // THEN
    assertThat(result)
        .isPresent();
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void getUserFromTest_whenIsGoogle(String credentials) {
    // GIVEN
    var oauthUser = mock(OAuth2UserInfo.class);
    when(googleOAuthService.validateTokenAndGetUser(credentials))
        .thenReturn(Optional.of(oauthUser));

    // WHEN
    var result = oAuthServiceAdapter.getUserFromToken(credentials, Provider.GOOGLE);

    // THEN
    assertThat(result)
        .isPresent();
  }

}