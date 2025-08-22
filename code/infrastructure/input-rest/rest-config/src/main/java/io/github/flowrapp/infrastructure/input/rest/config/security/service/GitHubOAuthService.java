package io.github.flowrapp.infrastructure.input.rest.config.security.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.config.security.value.GitHubUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHEmail;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;

/**
 * Service for validating GitHub OAuth2 access tokens and retrieving user information. Uses the GitHub API to validate tokens and fetch user
 * details including email addresses.
 */
@Slf4j
@Service
public class GitHubOAuthService {

  /**
   * Validates a GitHub access token and retrieves user information.
   *
   * @param accessToken the GitHub access token to validate
   * @return OAuth2UserInfo containing the user's information, or empty if validation fails
   */
  public Optional<OAuth2UserInfo> validateTokenAndGetUser(String accessToken) {
    try {
      // Create GitHub client with the access token
      GitHub github = new GitHubBuilder()
          .withOAuthToken(accessToken)
          .build();

      // Get the authenticated user
      GHUser user = github.getMyself();

      // Get user's primary email address
      String email = getPrimaryEmail(github, user);

      // Create GitHubUserInfo with the retrieved data
      GitHubUserInfo userInfo = new GitHubUserInfo(
          String.valueOf(user.getId()),
          email,
          user.getName() != null ? user.getName() : user.getLogin(),
          user.getAvatarUrl());

      log.debug("Successfully validated GitHub token for user: {}", user.getLogin());
      return Optional.of(userInfo);

    } catch (IOException e) {
      log.warn("Failed to validate GitHub access token: {}", e.getMessage());
      return Optional.empty();
    } catch (Exception e) {
      log.error("Unexpected error validating GitHub access token", e);
      return Optional.empty();
    }
  }

  /**
   * Retrieves the user's primary email address from GitHub. Falls back to the public email if primary email is not available.
   *
   * @param github the GitHub client
   * @param user the GitHub user
   * @return the user's email address, or null if not available
   */
  private String getPrimaryEmail(GitHub github, GHUser user) {
    try {
      // Try to get emails from the API (requires user:email scope)
      List<GHEmail> emails = github.getMyself().getEmails2();

      // Find the primary email
      Optional<GHEmail> primaryEmail = emails.stream()
          .filter(GHEmail::isPrimary)
          .findFirst();

      if (primaryEmail.isPresent()) {
        return primaryEmail.get().getEmail();
      }

      // Fallback to any verified email
      Optional<GHEmail> verifiedEmail = emails.stream()
          .filter(GHEmail::isVerified)
          .findFirst();

      if (verifiedEmail.isPresent()) {
        return verifiedEmail.get().getEmail();
      }

      // Fallback to first email if no primary/verified found
      if (!emails.isEmpty()) {
        return emails.get(0).getEmail();
      }

    } catch (IOException e) {
      log.debug("Could not retrieve emails from GitHub API, trying public email: {}", e.getMessage());
    }

    try {
      // Fallback to public email if email API is not accessible
      return user.getEmail();
    } catch (IOException e) {
      log.warn("Could not retrieve public email from GitHub: {}", e.getMessage());
      return null;
    }
  }
}
