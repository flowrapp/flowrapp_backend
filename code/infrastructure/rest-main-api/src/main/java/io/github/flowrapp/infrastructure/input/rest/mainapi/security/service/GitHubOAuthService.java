package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import static java.util.function.Predicate.not;

import java.io.IOException;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.value.GitHubUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.kohsuke.github.GHEmail;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for validating GitHub OAuth2 access tokens and retrieving user information. Uses GitHub REST API to validate tokens and fetch
 * user details including email addresses.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GitHubOAuthService {

  private static final String GITHUB_NO_REPLY_EMAIL_DOMAIN = "users.noreply.github.com";

  @Qualifier("githubOAuthRestTemplate")
  private final RestTemplate restTemplate;

  private final ObjectProvider<GitHubBuilder> gitHubBuilderProvider;

  /**
   * Retrieves user information from GitHub API using the provided access token.
   */
  @SneakyThrows
  public Optional<OAuth2UserInfo> validateTokenAndGetUser(String accessToken) {
    log.debug("Getting user info from GitHub API.");

    val github = gitHubBuilderProvider.getObject()
        .withOAuthToken(accessToken)
        .build();

    val githubUser = github.getMyself();
    if (githubUser == null) {
      log.warn("Failed to retrieve user info from GitHub API.");
      return Optional.empty();
    }

    val login = githubUser.getLogin();
    val displayName = Optional.ofNullable(githubUser.getName())
        .filter(not(String::isBlank))
        .orElse(login);

    val mail = Optional.ofNullable(githubUser.getEmail())
        .or(() -> this.getPrimaryEmail(githubUser))
        .orElseGet(() -> login + "@" + GITHUB_NO_REPLY_EMAIL_DOMAIN); // Fallback to GitHub's no-reply email if no email is available

    return Optional.of(
        GitHubUserInfo.builder()
            .id(String.valueOf(githubUser.getId()))
            .email(mail)
            .name(displayName)
            .avatarUrl(githubUser.getAvatarUrl())
            .build());
  }

  /**
   * For some reason githubUser.getEmail() often returns null, it has to be public and usually isn't. This method fetches the list of emails
   * via the GitHub API and returns the primary email if available.
   */
  private Optional<String> getPrimaryEmail(GHMyself myself) {
    try {
      log.debug("Fetching user emails from GitHub API.");
      return myself.listEmails().toList().stream()
          .filter(GHEmail::isPrimary)
          .map(GHEmail::getEmail)
          .findFirst();

    } catch (IOException e) {
      log.error("Error while fetching primary email from GitHub API: {}", e.getMessage());
      return Optional.empty();
    }
  }

}
