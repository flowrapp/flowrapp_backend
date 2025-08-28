package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.RequestEntity.get;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.value.GitHubUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for validating GitHub OAuth2 access tokens and retrieving user information. Uses GitHub REST API to validate tokens and fetch
 * user details including email addresses.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GitHubOAuthService {

  private static final String GITHUB_EMAILS_URL = "https://api.github.com/user/emails";

  private static final String GITHUB_NO_REPLY_EMAIL_DOMAIN = "users.noreply.github.com";

  private static final String GITHUB_MAIL_ACCEPT_HEADER = "application/vnd.github+json";

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

    val username = Optional.ofNullable(githubUser.getName())
        .filter(String::isBlank)
        .orElseGet(githubUser::getLogin);

    val mail = Optional.ofNullable(githubUser.getEmail())
        .or(() -> this.getPrimaryEmail(accessToken))
        .orElseGet(() -> username + GITHUB_NO_REPLY_EMAIL_DOMAIN); // Fallback to GitHub's no-reply email if no email is available

    return Optional.of(
        GitHubUserInfo.builder()
            .id(String.valueOf(githubUser.getId()))
            .email(mail)
            .name(username)
            .avatarUrl(githubUser.getAvatarUrl())
            .build());
  }

  private Optional<String> getPrimaryEmail(String accessToken) {
    try {
      ResponseEntity<List<GithubEmailBody>> response = restTemplate.exchange(get(GITHUB_EMAILS_URL)
          .accept(MediaType.parseMediaType(GITHUB_MAIL_ACCEPT_HEADER))
          .header(AUTHORIZATION, "Bearer " + accessToken)
          .build(), new ParameterizedTypeReference<>() {});

      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        log.warn("Failed to retrieve primary email from GitHub API.");
        return Optional.empty();
      }

      return response.getBody().stream()
          .filter(GithubEmailBody::primary)
          .findFirst()
          .map(GithubEmailBody::email);

    } catch (RestClientException e) {
      log.error("Error while fetching primary email from GitHub API: {}", e.getMessage());
      return Optional.empty();
    }
  }

  record GithubEmailBody(
      String email,
      boolean primary,
      boolean verified,
      String visibility) {

  }

}
