package io.github.flowrapp.infrastructure.input.rest.config.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.config.security.value.GitHubUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.kohsuke.github.GHEmail;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class GitHubOAuthServiceTest {

  private GitHubOAuthService gitHubOAuthService;

  @BeforeEach
  void setUp() {
    gitHubOAuthService = new GitHubOAuthService();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithPrimaryEmail(String accessToken, long userId, String email, String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);
    GHMyself mockMyself = mock(GHMyself.class);
    GHEmail mockEmail = mock(GHEmail.class);
    List<GHEmail> emails = Collections.singletonList(mockEmail);

    when(mockMyself.getId()).thenReturn(userId);
    when(mockMyself.getName()).thenReturn(name);
    when(mockMyself.getLogin()).thenReturn(login);
    when(mockMyself.getAvatarUrl()).thenReturn(avatarUrl);
    when(mockMyself.getEmails2()).thenReturn(emails);
    when(mockGitHub.getMyself()).thenReturn(mockMyself);
    when(mockEmail.isPrimary()).thenReturn(true);
    when(mockEmail.getEmail()).thenReturn(email);

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isPresent();
      OAuth2UserInfo userInfo = result.get();
      assertThat(userInfo).isInstanceOf(GitHubUserInfo.class);
      assertThat(userInfo.getId()).isEqualTo(String.valueOf(userId));
      assertThat(userInfo.getEmail()).isEqualTo(email);
      assertThat(userInfo.getName()).isEqualTo(name);
      assertThat(userInfo.getAvatarUrl()).isEqualTo(avatarUrl);
      assertThat(userInfo.getProvider()).isEqualTo(OAuth2UserInfo.Provider.GITHUB);
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithVerifiedEmailFallback(String accessToken, long userId, String email, String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);
    GHMyself mockMyself = mock(GHMyself.class);
    GHEmail mockPrimaryEmail = mock(GHEmail.class);
    GHEmail mockVerifiedEmail = mock(GHEmail.class);
    List<GHEmail> emails = Arrays.asList(mockPrimaryEmail, mockVerifiedEmail);

    when(mockMyself.getId()).thenReturn(userId);
    when(mockMyself.getName()).thenReturn(name);
    when(mockMyself.getLogin()).thenReturn(login);
    when(mockMyself.getAvatarUrl()).thenReturn(avatarUrl);
    when(mockMyself.getEmails2()).thenReturn(emails);
    when(mockGitHub.getMyself()).thenReturn(mockMyself);

    // No primary email
    when(mockPrimaryEmail.isPrimary()).thenReturn(false);
    when(mockPrimaryEmail.isVerified()).thenReturn(false);

    // But verified email exists
    when(mockVerifiedEmail.isPrimary()).thenReturn(false);
    when(mockVerifiedEmail.isVerified()).thenReturn(true);
    when(mockVerifiedEmail.getEmail()).thenReturn(email);

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isPresent();
      OAuth2UserInfo userInfo = result.get();
      assertThat(userInfo.getEmail()).isEqualTo(email);
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithFirstEmailFallback(String accessToken, long userId, String email, String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);
    GHMyself mockMyself = mock(GHMyself.class);
    GHEmail mockEmail = mock(GHEmail.class);
    List<GHEmail> emails = Collections.singletonList(mockEmail);

    when(mockMyself.getId()).thenReturn(userId);
    when(mockMyself.getName()).thenReturn(name);
    when(mockMyself.getLogin()).thenReturn(login);
    when(mockMyself.getAvatarUrl()).thenReturn(avatarUrl);
    when(mockMyself.getEmails2()).thenReturn(emails);
    when(mockGitHub.getMyself()).thenReturn(mockMyself);

    // Email is neither primary nor verified
    when(mockEmail.isPrimary()).thenReturn(false);
    when(mockEmail.isVerified()).thenReturn(false);
    when(mockEmail.getEmail()).thenReturn(email);

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isPresent();
      OAuth2UserInfo userInfo = result.get();
      assertThat(userInfo.getEmail()).isEqualTo(email);
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithPublicEmailFallback(String accessToken, long userId, String email, String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);
    GHMyself mockMyself = mock(GHMyself.class);

    when(mockMyself.getId()).thenReturn(userId);
    when(mockMyself.getName()).thenReturn(name);
    when(mockMyself.getLogin()).thenReturn(login);
    when(mockMyself.getAvatarUrl()).thenReturn(avatarUrl);
    when(mockMyself.getEmails2()).thenThrow(new IOException("Email API not accessible"));
    when(mockMyself.getEmail()).thenReturn(email);
    when(mockGitHub.getMyself()).thenReturn(mockMyself);

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isPresent();
      OAuth2UserInfo userInfo = result.get();
      assertThat(userInfo.getEmail()).isEqualTo(email);
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithNoEmail(String accessToken, long userId, String name, String login,
      String avatarUrl) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);
    GHMyself mockMyself = mock(GHMyself.class);

    when(mockMyself.getId()).thenReturn(userId);
    when(mockMyself.getName()).thenReturn(name);
    when(mockMyself.getLogin()).thenReturn(login);
    when(mockMyself.getAvatarUrl()).thenReturn(avatarUrl);

    // All email retrieval fails
    when(mockMyself.getEmails2()).thenThrow(new IOException("Email API not accessible"));
    when(mockMyself.getEmail()).thenThrow(new IOException("Public email not accessible"));
    when(mockGitHub.getMyself()).thenReturn(mockMyself);

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isPresent();
      OAuth2UserInfo userInfo = result.get();
      assertThat(userInfo.getEmail()).isNull();
      assertThat(userInfo.getName()).isEqualTo(name);
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithNullNameFallbackToLogin(String accessToken, long userId, String login,
      String avatarUrl) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);
    GHMyself mockMyself = mock(GHMyself.class);

    when(mockMyself.getId()).thenReturn(userId);
    when(mockMyself.getName()).thenReturn(null); // Null name
    when(mockMyself.getLogin()).thenReturn(login);
    when(mockMyself.getAvatarUrl()).thenReturn(avatarUrl);

    // Email API fails
    when(mockMyself.getEmails2()).thenThrow(new IOException("Email API not accessible"));
    when(mockMyself.getEmail()).thenThrow(new IOException("Public email not accessible"));
    when(mockGitHub.getMyself()).thenReturn(mockMyself);

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isPresent();
      OAuth2UserInfo userInfo = result.get();
      assertThat(userInfo.getName()).isEqualTo(login); // Should fallback to login
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithEmptyEmailList(String accessToken, long userId, String name, String login,
      String avatarUrl, String publicEmail) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);
    GHMyself mockMyself = mock(GHMyself.class);

    when(mockMyself.getId()).thenReturn(userId);
    when(mockMyself.getName()).thenReturn(name);
    when(mockMyself.getLogin()).thenReturn(login);
    when(mockMyself.getAvatarUrl()).thenReturn(avatarUrl);

    // Empty email list
    when(mockMyself.getEmails2()).thenReturn(Collections.emptyList());
    // Fallback to public email
    when(mockMyself.getEmail()).thenReturn(publicEmail);
    when(mockGitHub.getMyself()).thenReturn(mockMyself);

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isPresent();
      OAuth2UserInfo userInfo = result.get();
      assertThat(userInfo.getEmail()).isEqualTo(publicEmail);
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_IOException_ReturnsEmpty(String accessToken) throws Exception {
    // GIVEN
    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenThrow(new IOException("Network error"));

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isEmpty();
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_GitHubAPIException_ReturnsEmpty(String accessToken) throws Exception {
    // GIVEN
    GitHub mockGitHub = mock(GitHub.class);

    when(mockGitHub.getMyself()).thenThrow(new IOException("GitHub API error"));

    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenReturn(mockGitHub);

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isEmpty();
    }
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_UnexpectedException_ReturnsEmpty(String accessToken) throws Exception {
    // GIVEN
    try (MockedStatic<GitHubBuilder> mockedBuilder = mockStatic(GitHubBuilder.class)) {
      GitHubBuilder mockBuilder = mock(GitHubBuilder.class);
      mockedBuilder.when(GitHubBuilder::new).thenReturn(mockBuilder);
      when(mockBuilder.withOAuthToken(accessToken)).thenReturn(mockBuilder);
      when(mockBuilder.build()).thenThrow(new RuntimeException("Unexpected error"));

      // WHEN
      Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

      // THEN
      assertThat(result).isEmpty();
    }
  }

  @Test
  void validateTokenAndGetUser_NullAccessToken_ReturnsEmpty() {
    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(null);

    // THEN - Should handle gracefully without throwing exception
    // The actual behavior depends on how GitHubBuilder handles null tokens
    // This test ensures no unexpected exceptions are thrown
    assertThat(result).isEmpty();
  }
}
