package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.value.OAuth2UserInfo;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.kohsuke.github.GHEmail;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class GitHubOAuthServiceTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ObjectProvider<GitHubBuilder> gitHubBuilderProvider;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private GitHubBuilder gitHubBuilder;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private GHMyself myself;

  @InjectMocks
  private GitHubOAuthService gitHubOAuthService;

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void validateTokenAndGetUser_InvalidToken(String accessToken) throws Exception {
    // GIVEN
    when(gitHubBuilderProvider.getObject())
        .thenReturn(gitHubBuilder);
    when(gitHubBuilder.withOAuthToken(accessToken).build().getMyself())
        .thenReturn(null);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isNotPresent();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithPrimaryEmailFromApi(String accessToken, Long id, String login,
      String username, String mail, String avatarUrl) throws Exception {
    // GIVEN
    when(myself.getId())
        .thenReturn(id);
    when(myself.getLogin())
        .thenReturn(login);
    when(myself.getName())
        .thenReturn(username);
    when(myself.getAvatarUrl())
        .thenReturn(avatarUrl);
    when(myself.getEmail())
        .thenReturn(mail);

    when(gitHubBuilderProvider.getObject())
        .thenReturn(gitHubBuilder);
    when(gitHubBuilder.withOAuthToken(accessToken).build().getMyself())
        .thenReturn(myself);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result)
        .isPresent()
        .get()
        .returns(String.valueOf(myself.getId()), OAuth2UserInfo::getId)
        .returns(myself.getEmail(), OAuth2UserInfo::getEmail)
        .returns(myself.getName(), OAuth2UserInfo::getName)
        .returns(myself.getAvatarUrl(), OAuth2UserInfo::getAvatarUrl)
        .returns(OAuth2UserInfo.Provider.GITHUB, OAuth2UserInfo::getProvider);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithPrimaryFromFallbackApi(String accessToken, Long id, String login,
      String username, String avatarUrl) throws Exception {
    // GIVEN
    var mail = Instancio.of(GHEmail.class)
        .set(field(GHEmail::isPrimary), true)
        .create();

    when(myself.getId())
        .thenReturn(id);
    when(myself.getLogin())
        .thenReturn(login);
    when(myself.getName())
        .thenReturn(username);
    when(myself.getAvatarUrl())
        .thenReturn(avatarUrl);
    when(myself.getEmail())
        .thenReturn(null);
    when(myself.listEmails().toList())
        .thenReturn(List.of(mail));

    when(gitHubBuilderProvider.getObject())
        .thenReturn(gitHubBuilder);
    when(gitHubBuilder.withOAuthToken(accessToken).build().getMyself())
        .thenReturn(myself);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result)
        .isPresent()
        .get()
        .returns(String.valueOf(myself.getId()), OAuth2UserInfo::getId)
        .returns(mail.getEmail(), OAuth2UserInfo::getEmail)
        .returns(myself.getName(), OAuth2UserInfo::getName)
        .returns(myself.getAvatarUrl(), OAuth2UserInfo::getAvatarUrl)
        .returns(OAuth2UserInfo.Provider.GITHUB, OAuth2UserInfo::getProvider);
  }

}
