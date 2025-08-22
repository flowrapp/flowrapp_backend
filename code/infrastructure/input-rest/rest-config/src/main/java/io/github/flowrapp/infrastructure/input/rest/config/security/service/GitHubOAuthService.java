package io.github.flowrapp.infrastructure.input.rest.config.security.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.config.security.value.GitHubUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for validating GitHub OAuth2 access tokens and retrieving user information. Uses GitHub REST API to validate tokens and fetch
 * user details including email addresses.
 */
@Slf4j
@Service
public class GitHubOAuthService {

  private static final String GITHUB_USER_URL = "https://api.github.com/user";

  private static final String GITHUB_EMAILS_URL = "https://api.github.com/user/emails";

  private final HttpClient httpClient;

  private final ObjectMapper objectMapper;

  public GitHubOAuthService() {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Validates a GitHub access token and retrieves user information.
   *
   * @param accessToken the GitHub access token to validate
   * @return OAuth2UserInfo containing the user's information, or empty if validation fails
   */
  public Optional<OAuth2UserInfo> validateTokenAndGetUser(String accessToken) {
    try {
      log.debug("Validating GitHub access token");

      // Get user information from GitHub API
      Optional<JsonNode> userInfo = getUserInfo(accessToken);
      if (userInfo.isEmpty()) {
        return Optional.empty();
      }

      JsonNode user = userInfo.get();

      // Extract basic user information
      String userId = user.get("id").asText();
      String login = user.get("login").asText();
      String name = user.has("name") && !user.get("name").isNull() ? user.get("name").asText() : login;
      String avatarUrl = user.has("avatar_url") ? user.get("avatar_url").asText() : null;

      // Get user's email address
      String email = getPrimaryEmail(accessToken, user);

      GitHubUserInfo gitHubUserInfo = new GitHubUserInfo(
          userId,
          email,
          name,
          avatarUrl);

      log.debug("Successfully validated GitHub access token for user: {}", login);
      return Optional.of(gitHubUserInfo);

    } catch (Exception e) {
      log.error("Unexpected error during GitHub access token validation", e);
      return Optional.empty();
    }
  }

  /**
   * Retrieves user information from GitHub API.
   *
   * @param accessToken the GitHub access token
   * @return JsonNode containing user information, or empty if request fails
   */
  private Optional<JsonNode> getUserInfo(String accessToken) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(GITHUB_USER_URL))
          .timeout(Duration.ofSeconds(10))
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/vnd.github.v3+json")
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        log.warn("GitHub user API returned status code: {}", response.statusCode());
        return Optional.empty();
      }

      JsonNode userInfo = objectMapper.readTree(response.body());
      return Optional.of(userInfo);

    } catch (IOException | InterruptedException e) {
      log.error("Error calling GitHub user API: {}", e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Retrieves the user's primary email address from GitHub. Falls back to public email if private emails are not accessible.
   *
   * @param accessToken the GitHub access token
   * @param user the user information from GitHub API
   * @return the user's email address, or null if not available
   */
  private String getPrimaryEmail(String accessToken, JsonNode user) {
    // Try to get emails from the emails API (requires user:email scope)
    Optional<String> emailFromApi = getEmailFromApi(accessToken);
    if (emailFromApi.isPresent()) {
      return emailFromApi.get();
    }

    // Fallback to public email from user object
    if (user.has("email") && !user.get("email").isNull()) {
      String publicEmail = user.get("email").asText();
      if (publicEmail != null && !publicEmail.trim().isEmpty()) {
        return publicEmail;
      }
    }

    log.debug("No email address available for GitHub user");
    return null;
  }

  /**
   * Retrieves email addresses from GitHub emails API.
   *
   * @param accessToken the GitHub access token
   * @return the primary or first available email, or empty if not accessible
   */
  private Optional<String> getEmailFromApi(String accessToken) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(GITHUB_EMAILS_URL))
          .timeout(Duration.ofSeconds(10))
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/vnd.github.v3+json")
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        log.debug("GitHub emails API returned status code: {} (may lack user:email scope)", response.statusCode());
        return Optional.empty();
      }

      JsonNode emails = objectMapper.readTree(response.body());

      // Find primary email
      for (JsonNode email : emails) {
        if (email.has("primary") && email.get("primary").asBoolean()) {
          return Optional.of(email.get("email").asText());
        }
      }

      // Fallback to first verified email
      for (JsonNode email : emails) {
        if (email.has("verified") && email.get("verified").asBoolean()) {
          return Optional.of(email.get("email").asText());
        }
      }

      // Fallback to first email if no primary/verified found
      if (!emails.isEmpty()) {
        return Optional.of(emails.get(0).get("email").asText());
      }

    } catch (IOException | InterruptedException e) {
      log.debug("Could not retrieve emails from GitHub API: {}", e.getMessage());
    }

    return Optional.empty();
  }
}
